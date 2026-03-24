import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parent
WEB_BUILD = ROOT / "web-build"
TEST_WORLDS = WEB_BUILD / "test-worlds"
MODS_SRC = ROOT / "mods"
WORLD_NAME = "mods"

# Path to the template level.dat found in MCP sources
LEVEL_DAT_TEMPLATE = ROOT / "temp_mcp_extract" / "jars" / "saves" / "MCP Development World" / "level.dat"

def ensure_mods_world():
    world_dir = TEST_WORLDS / WORLD_NAME
    world_dir.mkdir(parents=True, exist_ok=True)
    print(f"[INFO] Syncing mods for test world: {WORLD_NAME}")

    mod_world_dir = world_dir / "mods"
    mod_world_dir.mkdir(parents=True, exist_ok=True)
    mod_files = sorted([p for p in MODS_SRC.glob("*.jar") if p.is_file()])
    if not mod_files:
        print(f"[WARNING] No mod jars found in {MODS_SRC}")
    for mod_file in mod_files:
        print(f"[INFO] Installing mod {mod_file.name} to world {WORLD_NAME}")
        shutil.copy2(mod_file, mod_world_dir / mod_file.name)
    mods_list_path = mod_world_dir / "mods_list.txt"
    mods_list_path.write_text("\n".join([m.name for m in mod_files]) + ("\n" if mod_files else ""), encoding="utf-8")

    # Update worlds_list.txt (plain text for easier Java parsing)
    worlds_list_path = TEST_WORLDS / "worlds_list.txt"
    worlds = []
    if worlds_list_path.exists():
        with open(worlds_list_path, "r") as f:
            worlds = f.read().splitlines()
    
    if WORLD_NAME not in worlds:
        worlds.append(WORLD_NAME)
        
    with open(worlds_list_path, "w") as f:
        f.write("\n".join(worlds) + "\n")

    print(f"[SUCCESS] World '{WORLD_NAME}' is ready for testing.")

if __name__ == "__main__":
    ensure_mods_world()
