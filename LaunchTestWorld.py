import subprocess
import sys
import os
from pathlib import Path

ROOT = Path(__file__).resolve().parent

def main():
    world_name = "mods"
    if len(sys.argv) > 1:
        world_name = sys.argv[1]

    print(f"==================================================")
    print(f"   Eaglercraft Fast-Launch Test World: {world_name}")
    print(f"==================================================")

    # 1. Sync world and mods
    print(f"[1/2] Preparing world and syncing mods...")
    print(f"      - Note: If '{world_name}' doesn't exist, the game will create a NEW FLAT world automatically.")
    manage_script = ROOT / "ManageTestWorld.py"
    if manage_script.is_file():
        subprocess.run([sys.executable, str(manage_script)], cwd=ROOT)
    else:
        print("[WARNING] ManageTestWorld.py not found, skipping sync.")

    # 2. Launch the game with the world parameter
    print(f"[2/2] Launching Eaglercraft on port 8090...")
    run_script = ROOT / "RunWebGame.py"
    if run_script.is_file():
        # We use --skip-build to launch faster if it's already built.
        # Use --world to trigger the auto-load logic we added.
        cmd = [sys.executable, str(run_script), "--world", world_name, "--skip-build"]
        try:
            subprocess.run(cmd, cwd=ROOT)
        except KeyboardInterrupt:
            print("\n[INFO] Game server stopped.")
    else:
        print("[ERROR] RunWebGame.py not found! Cannot launch.")

if __name__ == "__main__":
    main()
