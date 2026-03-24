import os
import shutil
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent
VANILLA_1_8_DIR = ROOT / "##TEAVM.TMP##" / "vanilla_1_8" / "assets" / "minecraft" / "textures"
MODERN_1_21_DIR = Path(r"C:\Users\Sidivall AI\Desktop\ماين كرافت جافا\مشروع يحتوي عل جميع كافة ملفات ماين كرافت\1.21.8")
OVERRIDE_DIR = ROOT / "sources" / "resources" / "assets" / "minecraft" / "textures"
REPORT_PATH = ROOT / "comprehensive_migration_report.json"

SKIP_PREFIXES = [
    "font", "particle", "painting", "gui",
    "entity/chest", "entity/bed", "entity/horse",
    "entity/villager", "entity/zombie_villager",
    "entity/banner", "entity/shield", "entity/shulker",
    "entity/boat", "entity/llama", "entity/illager",
    "entity/enderdragon", "entity/pig/pig_saddle",
    "blocks"  # blocks are already handled by SyncModernTextures.py and overrides
]

ALIASES = {
    # Items
    "beef_cooked": "cooked_beef", "beef_raw": "beef",
    "chicken_cooked": "cooked_chicken", "chicken_raw": "chicken",
    "fish_cod_raw": "cod", "fish_cod_cooked": "cooked_cod",
    "fish_salmon_raw": "salmon", "fish_salmon_cooked": "cooked_salmon",
    "fish_pufferfish_raw": "pufferfish", "fish_clownfish_raw": "tropical_fish",
    "mutton_raw": "mutton", "mutton_cooked": "cooked_mutton",
    "porkchop_raw": "porkchop", "porkchop_cooked": "cooked_porkchop",
    "rabbit_raw": "rabbit", "rabbit_cooked": "cooked_rabbit",
    "potato_baked": "baked_potato", "potato_poisonous": "poisonous_potato",
    "carrot_golden": "golden_carrot", "apple_golden": "golden_apple",
    "melon": "melon_slice", "spider_eye_fermented": "fermented_spider_eye",
    "speckled_melon": "glistering_melon_slice",
    "book_normal": "book", "book_enchanted": "enchanted_book",
    "book_writable": "book_and_quill", "book_written": "written_book",
    "potion_bottle_empty": "glass_bottle", "potion_bottle_drinkable": "potion",
    "potion_bottle_splash": "splash_potion", "bucket_empty": "bucket",
    "bucket_water": "water_bucket", "bucket_lava": "lava_bucket", "bucket_milk": "milk_bucket",
    "dye_powder_black": "ink_sac", "dye_powder_red": "red_dye", "dye_powder_green": "green_dye",
    "dye_powder_brown": "cocoa_beans", "dye_powder_blue": "lapis_lazuli",
    "dye_powder_purple": "purple_dye", "dye_powder_cyan": "cyan_dye",
    "dye_powder_silver": "light_gray_dye", "dye_powder_gray": "gray_dye",
    "dye_powder_pink": "pink_dye", "dye_powder_lime": "lime_dye",
    "dye_powder_yellow": "yellow_dye", "dye_powder_light_blue": "light_blue_dye",
    "dye_powder_magenta": "magenta_dye", "dye_powder_orange": "orange_dye",
    "dye_powder_white": "bone_meal", "door_wood": "oak_door", "door_iron": "iron_door",
    "door_spruce": "spruce_door", "door_birch": "birch_door", "door_jungle": "jungle_door",
    "door_acacia": "acacia_door", "door_dark_oak": "dark_oak_door",
    "boat": "oak_boat", "minecart_normal": "minecart", "minecart_chest": "chest_minecart",
    "minecart_furnace": "minecart", "minecart_hopper": "hopper_minecart",
    "minecart_tnt": "tnt_minecart", "minecart_command_block": "command_block_minecart",
    "slimeball": "slime_ball", "snowball": "snowball", "seeds_wheat": "wheat_seeds",
    "seeds_pumpkin": "pumpkin_seeds", "seeds_melon": "melon_seeds",
    "fireballs": "fire_charge", "bow_standby": "bow", "fishing_rod_uncast": "fishing_rod",
    "gold_axe": "golden_axe", "gold_hoe": "golden_hoe", "gold_pickaxe": "golden_pickaxe",
    "gold_shovel": "golden_shovel", "gold_sword": "golden_sword", "wood_axe": "wooden_axe",
    "wood_hoe": "wooden_hoe", "wood_pickaxe": "wooden_pickaxe", "wood_shovel": "wooden_shovel",
    "wood_sword": "wooden_sword", "clock": "clock_00", "compass": "compass_00",
    "reeds": "sugar_cane", "record_11": "music_disc_11", "record_13": "music_disc_13",
    "record_blocks": "music_disc_blocks", "record_cat": "music_disc_cat",
    "record_chirp": "music_disc_chirp", "record_far": "music_disc_far",
    "record_mall": "music_disc_mall", "record_mellohi": "music_disc_mellohi",
    "record_stal": "music_disc_stal", "record_strad": "music_disc_strad",
    "record_wait": "music_disc_wait", "record_ward": "music_disc_ward",
    "quiver": "quiver", # might not exist
}

def build_modern_index():
    idx = {}
    for p in MODERN_1_21_DIR.rglob("*.png"):
        stem = p.stem
        if stem not in idx: idx[stem] = []
        idx[stem].append(p)
    return idx

def get_modern_file(idx, names_to_check, preferred_parent):
    for n in names_to_check:
        if n in idx:
            paths = idx[n]
            for p in paths:
                if preferred_parent in p.parts:
                    return p
            return paths[0]
    return None

def source_candidates(target_name):
    c = [target_name]
    if target_name in ALIASES:
        c.append(ALIASES[target_name])
    return c

def main():
    if not VANILLA_1_8_DIR.is_dir():
        print("Vanilla 1.8 directory missing. Run extraction first.")
        return

    modern_index = build_modern_index()
    results = {"copied": 0, "skipped_by_prefix": 0, "unresolved": 0, "details": []}

    for p in VANILLA_1_8_DIR.rglob("*.png"):
        rel_path = p.relative_to(VANILLA_1_8_DIR)
        rel_str = rel_path.as_posix()
        
        skip = False
        for prefix in SKIP_PREFIXES:
            if rel_str.startswith(prefix):
                skip = True
                break
        
        if skip:
            results["skipped_by_prefix"] += 1
            continue

        target_name = p.stem
        candidates = source_candidates(target_name)
        preferred_parent = rel_path.parent.name
        
        modern_p = get_modern_file(modern_index, candidates, preferred_parent)
        
        if modern_p:
            out_file = OVERRIDE_DIR / rel_path
            out_file.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(modern_p, out_file)
            results["copied"] += 1
            results["details"].append({"vanilla": rel_str, "modern": modern_p.as_posix()})
        else:
            results["unresolved"] += 1
            
    REPORT_PATH.write_text(json.dumps(results, indent=2))
    print(f"Done! Copied: {results['copied']}, Unresolved: {results['unresolved']}, Skipped (Prefix): {results['skipped_by_prefix']}")

if __name__ == "__main__":
    main()
