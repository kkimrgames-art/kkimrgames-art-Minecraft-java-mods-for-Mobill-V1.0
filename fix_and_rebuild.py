#!/usr/bin/env python3
"""Fix encoding issues in .edit.java files and rebuild the web game."""
import os, shutil, subprocess, sys

BASE = os.path.dirname(os.path.abspath(__file__))

def normalize_file(path):
    """Normalize a file to UTF-8 with Unix LF line endings (no BOM)."""
    with open(path, 'rb') as f:
        data = f.read()
    # Remove BOM if present
    if data.startswith(b'\xef\xbb\xbf'):
        data = data[3:]
    # Normalize to LF
    data = data.replace(b'\r\n', b'\n').replace(b'\r', b'\n')
    with open(path, 'wb') as f:
        f.write(data)
    newline = b'\n'
    line_count = data.count(newline)
    print(f"  Normalized: {os.path.basename(path)} ({len(data)} bytes, {line_count} lines)")

# Step 1: Normalize ALL .edit.java patch files
print("=== Step 1: Normalizing patch file encoding ===")
patches_dir = os.path.join(BASE, "patches", "minecraft")
count = 0
for root, dirs, files in os.walk(patches_dir):
    for fn in files:
        if fn.endswith('.edit.java'):
            normalize_file(os.path.join(root, fn))
            count += 1
print(f"  Normalized {count} patch files.\n")

# Step 2: Delete TEAVM.TMP to force clean rebuild
tmp_dir = os.path.join(BASE, "##TEAVM.TMP##")
if os.path.isdir(tmp_dir):
    print("=== Step 2: Deleting ##TEAVM.TMP## for clean rebuild ===")
    shutil.rmtree(tmp_dir, ignore_errors=True)
    print("  Deleted.\n")
else:
    print("=== Step 2: ##TEAVM.TMP## already clean ===\n")

# Step 3: Run the build
print("=== Step 3: Running RunWebGame.py ===")
sys.stdout.flush()
os.chdir(BASE)
ret = subprocess.call([sys.executable, "RunWebGame.py"], cwd=BASE)
print(f"\n=== Build finished with exit code: {ret} ===")
