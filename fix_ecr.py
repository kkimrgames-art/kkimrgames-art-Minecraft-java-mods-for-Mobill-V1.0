#!/usr/bin/env python3
"""Fix corrupted ECR patch file by re-syncing block headers with actual line counts."""
import os, re, sys, shutil

target = os.path.join(os.path.dirname(os.path.abspath(__file__)),
    "patches", "minecraft", "net", "minecraft", "client", "gui", "GuiMainMenu.edit.java")

print(f"Reading: {target}")
with open(target, 'rb') as f:
    raw = f.read()

# Strip BOM
if raw.startswith(b'\xef\xbb\xbf'):
    raw = raw[3:]
    print("  Removed BOM")

# Normalize to LF
raw = raw.replace(b'\r\n', b'\n').replace(b'\r', b'\n')
text = raw.decode('utf-8')
lines = text.split('\n')

print(f"  Total lines: {len(lines)}")
print(f"  Total bytes: {len(raw)}")
print()

# Parse ECR blocks and check for mismatches
fixed_lines = []
i = 0
block_num = 0
fixes = 0
while i < len(lines):
    line = lines[i]

    # Check for block header
    m = re.match(r'^> (CHANGE|DELETE|INSERT)\s+(.*)$', line)
    if m:
        block_num += 1
        block_type = m.group(1)
        header_rest = m.group(2)
        
        # Parse targetLen from header
        parts = header_rest.strip().split()
        # Determine prefix for this block type
        if block_type == 'CHANGE':
            prefix = '~ '
        elif block_type == 'INSERT':
            prefix = '+ '
        elif block_type == 'DELETE':
            prefix = '- '
        else:
            prefix = None
            
        # Parse targetStart and targetEnd to get targetLen
        target_len = 0
        if ':' in parts[:3]:
            colon_idx = parts.index(':')
            if colon_idx == 1:  # "targetStart : targetEnd @ ..."
                try:
                    ts = int(parts[0])
                    te = int(parts[2])
                    target_len = te - ts
                except:
                    pass
        
        # Count actual content lines following the header
        # Skip blank line after header
        fixed_lines.append(line)
        i += 1
        # Skip blank line
        if i < len(lines) and lines[i].strip() == '':
            fixed_lines.append(lines[i])
            i += 1
        
        # Count content lines (starting with prefix)
        content_lines = []
        while i < len(lines) and lines[i].startswith(prefix[0]) and len(lines[i]) >= 2 and lines[i][1] == ' ':
            content_lines.append(lines[i])
            i += 1
        
        actual_count = len(content_lines)
        
        if target_len > 0 and actual_count != target_len:
            print(f"  Block #{block_num} ({block_type}): header says {target_len} lines, found {actual_count} lines")
            # Fix the header
            old_end_str = str(int(parts[0]) + target_len)
            new_end_str = str(int(parts[0]) + actual_count)
            new_header = line.replace(f" {old_end_str} ", f" {new_end_str} ", 1)
            # Replace the header in fixed_lines
            fixed_lines[-2 if fixed_lines[-1].strip() == '' else -1] = new_header
            print(f"    Fixed header: {line} -> {new_header}")
            fixes += 1
        
        fixed_lines.extend(content_lines)
    else:
        fixed_lines.append(line)
        i += 1

print(f"\n  Total fixes: {fixes}")

if fixes > 0:
    output = '\n'.join(fixed_lines)
    # Ensure no trailing newlines are duplicated
    with open(target, 'wb') as f:
        f.write(output.encode('utf-8'))
    print(f"  Written {len(output)} bytes")
else:
    print("  No fixes needed - the header/content counts all match!")
    print("  The issue might be elsewhere. Dumping first 30 lines as hex for inspection:")
    for idx, l in enumerate(lines[:30]):
        hex_repr = l.encode('utf-8').hex()
        print(f"    Line {idx+1}: [{l[:60]}] hex_end=[{hex_repr[-20:]}]")

# Also check for GuiScreen.edit.java
gs = os.path.join(os.path.dirname(target), "GuiScreen.edit.java")
print(f"\n--- Checking GuiScreen.edit.java ---")
with open(gs, 'rb') as f:
    gs_raw = f.read()
has_bom = gs_raw.startswith(b'\xef\xbb\xbf')
crlf_count = gs_raw.count(b'\r\n')
print(f"  BOM: {has_bom}, CRLF count: {crlf_count}, size: {len(gs_raw)}")
