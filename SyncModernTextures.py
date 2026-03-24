import argparse
import json
import shutil
import zipfile
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    Image = None

ROOT = Path(__file__).resolve().parent
DEFAULT_SOURCE = Path(r"C:\Users\Sidivall AI\Desktop\ماين كرافت جافا\مشروع يحتوي عل جميع كافة ملفات ماين كرافت\1.21.8")

TARGET_TEXTURES = ROOT / "sources" / "resources" / "assets" / "minecraft" / "textures"
REPORT_PATH = ROOT / "texture_migration_report.json"
JAR_1_8_8 = ROOT / "mcp918" / "1.8.8.jar"

ALIASES = {
    "bow_standby": "bow",
    "brick": "bricks",
    "bucket_lava": "lava_bucket",
    "bucket_milk": "milk_bucket",
    "bucket_water": "water_bucket",
    "clock": "clock_00",
    "cobblestone_mossy": "mossy_cobblestone",
    "comparator_off": "comparator",
    "compass": "compass_00",
    "dirt_podzol_side": "podzol_side",
    "dirt_podzol_top": "podzol_top",
    "dispenser_front_horizontal": "dispenser_front",
    "door_acacia_lower": "acacia_door_bottom",
    "door_acacia_upper": "acacia_door_top",
    "door_birch_lower": "birch_door_bottom",
    "door_birch_upper": "birch_door_top",
    "door_dark_oak_lower": "dark_oak_door_bottom",
    "door_dark_oak_upper": "dark_oak_door_top",
    "door_jungle_lower": "jungle_door_bottom",
    "door_jungle_upper": "jungle_door_top",
    "door_spruce_lower": "spruce_door_bottom",
    "door_spruce_upper": "spruce_door_top",
    "door_wood_lower": "oak_door_bottom",
    "door_wood_upper": "oak_door_top",
    "dropper_front_horizontal": "dropper_front",
    "endframe_side": "end_portal_frame_side",
    "endframe_top": "end_portal_frame_top",
    "farmland_dry": "farmland",
    "farmland_wet": "farmland_moist",
    "fishing_rod_uncast": "fishing_rod",
    "furnace_front_off": "furnace_front",
    "gold_axe": "golden_axe",
    "gold_hoe": "golden_hoe",
    "gold_pickaxe": "golden_pickaxe",
    "gold_shovel": "golden_shovel",
    "gold_sword": "golden_sword",
    "grass_side": "grass_block_side",
    "grass_side_overlay": "grass_block_side_overlay",
    "grass_side_snowed": "grass_block_snow",
    "grass_top": "grass_block_top",
    "leaves_big_oak": "dark_oak_leaves",
    "log_big_oak": "dark_oak_log",
    "log_big_oak_top": "dark_oak_log_top",
    "minecart_chest": "chest_minecart",
    "minecart_command_block": "command_block_minecart",
    "minecart_furnace": "minecart",
    "minecart_tnt": "tnt_minecart",
    "planks_big_oak": "dark_oak_planks",
    "prismarine_dark": "dark_prismarine",
    "prismarine_rough": "prismarine",
    "pumpkin_face_off": "carved_pumpkin",
    "pumpkin_face_on": "jack_o_lantern",
    "quartz_block_chiseled": "chiseled_quartz_block",
    "quartz_block_chiseled_top": "chiseled_quartz_block_top",
    "quartz_block_lines": "quartz_pillar",
    "quartz_block_lines_top": "quartz_pillar_top",
    "quartz_ore": "nether_quartz_ore",
    "rail_activator": "activator_rail",
    "rail_activator_powered": "activator_rail_on",
    "rail_detector": "detector_rail",
    "rail_detector_powered": "detector_rail_on",
    "rail_golden": "powered_rail",
    "rail_golden_powered": "powered_rail_on",
    "redstone_lamp_off": "redstone_lamp",
    "redstone_torch_on": "redstone_torch",
    "repeater_off": "repeater",
    "sandstone_carved": "chiseled_sandstone",
    "sandstone_smooth": "cut_sandstone",
    "stone_andesite": "andesite",
    "stone_andesite_smooth": "polished_andesite",
    "stone_diorite": "diorite",
    "stone_diorite_smooth": "polished_diorite",
    "stone_granite": "granite",
    "stone_granite_smooth": "polished_granite",
    "stone_slab_side": "smooth_stone_slab_side",
    "stone_slab_top": "smooth_stone",
    "stonebrick_carved": "chiseled_stone_bricks",
    "stonebrick_cracked": "cracked_stone_bricks",
    "stonebrick_mossy": "mossy_stone_bricks",
    "torch_on": "torch",
    "trapdoor": "oak_trapdoor",
    "trip_wire_source": "tripwire_hook",
}

def build_index(src_dir: Path):
    return {p.stem: p for p in src_dir.rglob("*.png")}

def source_candidates(target_name: str):
    candidates = [target_name]
    alias = ALIASES.get(target_name)
    if alias:
        candidates.append(alias)
    if target_name.startswith("planks_"):
        wood = target_name.removeprefix("planks_")
        candidates.append(f"{wood}_planks")
    if target_name.startswith("log_"):
        rest = target_name.removeprefix("log_")
        candidates.append(f"{rest}_log")
    if target_name.startswith("log_") and target_name.endswith("_top"):
        wood = target_name.removeprefix("log_").removesuffix("_top")
        candidates.append(f"{wood}_log_top")
    if target_name.endswith("_normal"):
        candidates.append(target_name.removesuffix("_normal"))
    if target_name == "rail_normal":
        candidates.append("rail")
    if target_name == "rail_normal_turned":
        candidates.append("rail_corner")
    if target_name == "stonebrick":
        candidates.append("stone_bricks")
    if target_name == "snow":
        candidates.append("snow_block")
    dedup = []
    seen = set()
    for c in candidates:
        if c not in seen:
            dedup.append(c)
            seen.add(c)
    return dedup

def apply_legacy_leaf_mapping():
    blocks_dir = TARGET_TEXTURES / "blocks"
    pairs = {
        "oak_leaves": "leaves_oak",
        "spruce_leaves": "leaves_spruce",
        "birch_leaves": "leaves_birch",
        "jungle_leaves": "leaves_jungle",
        "acacia_leaves": "leaves_acacia",
        "dark_oak_leaves": "leaves_big_oak",
    }
    written = 0
    for src, dst in pairs.items():
        src_file = blocks_dir / f"{src}.png"
        dst_file = blocks_dir / f"{dst}.png"
        if src_file.is_file():
            shutil.copy2(src_file, dst_file)
            written += 1
    return written

def apply_colormap_update(source_root: Path):
    src_colormap = source_root / "colormap"
    dst_colormap = TARGET_TEXTURES / "colormap"
    dst_colormap.mkdir(parents=True, exist_ok=True)
    written = 0
    for name in ("foliage.png", "grass.png"):
        s = src_colormap / name
        d = dst_colormap / name
        if s.is_file():
            shutil.copy2(s, d)
            written += 1
    return written

def apply_chicken_fix(source_root: Path):
    entity_dir = TARGET_TEXTURES / "entity"
    entity_dir.mkdir(parents=True, exist_ok=True)
    target = entity_dir / "chicken.png"
    candidates = [
        source_root / "entity" / "chicken" / "chicken.png",
        source_root / "entity" / "chicken.png",
    ]
    for c in candidates:
        if c.is_file():
            shutil.copy2(c, target)
            return str(c)
    if JAR_1_8_8.is_file():
        with zipfile.ZipFile(JAR_1_8_8, "r") as z:
            p = "assets/minecraft/textures/entity/chicken.png"
            if p in z.namelist():
                target.write_bytes(z.read(p))
                return f"{JAR_1_8_8}!/{p}"
    return None

def run(source_root: Path, apply_changes: bool):
    if not source_root.is_dir():
        raise FileNotFoundError(f"Source folder not found: {source_root}")

    print("Building source index...")
    # Build complete index of all 1.21.8 textures recursively
    source_index = build_index(source_root)
    
    report = {
        "source_root": str(source_root),
        "apply_changes": apply_changes,
        "categories": {},
        "post_fixes": {}
    }

    folders_to_check = ["blocks", "items", "gui"]
    
    total_replaced = 0
    total_scanned = 0

    for cat in folders_to_check:
        target_dir = TARGET_TEXTURES / cat
        if not target_dir.is_dir():
            continue
            
        cat_report = {
            "scanned": 0,
            "resolved": 0,
            "unresolved": 0,
            "written_details": []
        }
        
        # Iterating over all target textures
        for target_png in target_dir.rglob("*.png"):
            target_name = target_png.stem
            cat_report["scanned"] += 1
            total_scanned += 1
            
            found = None
            used_name = None
            for candidate in source_candidates(target_name):
                if candidate in source_index:
                    found = source_index[candidate]
                    used_name = candidate
                    break
            
            if found:
                cat_report["resolved"] += 1
                total_replaced += 1
                if apply_changes:
                    shutil.copy2(found, target_png)
                cat_report["written_details"].append({
                    "target": target_png.name,
                    "source_name": used_name,
                    "source_path": str(found)
                })
            else:
                cat_report["unresolved"] += 1
                
        report["categories"][cat] = cat_report
    
    # Bed textures fallback
    if apply_changes and Image is not None:
        target_blocks = TARGET_TEXTURES / "blocks"
        bed_src = source_root / "entity" / "bed" / "red.png"
        if bed_src.is_file() and target_blocks.is_dir():
            img = Image.open(bed_src).convert("RGBA")
            crops = {
                "bed_head_top": (6, 6, 22, 22),
                "bed_feet_top": (6, 22, 22, 38),
                "bed_head_side": (22, 6, 38, 22),
                "bed_feet_side": (22, 22, 38, 38),
                "bed_head_end": (38, 6, 54, 22),
                "bed_feet_end": (38, 22, 54, 38),
            }
            for name, box in crops.items():
                out_file = target_blocks / f"{name}.png"
                if out_file.exists(): 
                    # Overwrite existing parts
                    tile = img.crop(box).resize((16, 16), Image.NEAREST)
                    tile.save(out_file, format="PNG")
                    total_replaced += 1

    if apply_changes:
        report["post_fixes"]["legacy_leaf_files_written"] = apply_legacy_leaf_mapping()
        report["post_fixes"]["colormap_files_written"] = apply_colormap_update(source_root)
        chicken_src = apply_chicken_fix(source_root)
        report["post_fixes"]["chicken_texture_source"] = chicken_src

    report["total_scanned"] = total_scanned
    report["total_replaced"] = total_replaced
    REPORT_PATH.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")

    return report

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", default=str(DEFAULT_SOURCE))
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()

    report = run(Path(args.source), args.apply)
    print(f"المصدر: {report['source_root']}")
    for cat, data in report["categories"].items():
        print(f"المجلد {cat}: تم إيجاد {data['resolved']}/{data['scanned']} صور.")
    print(f"إجمالي الصور المستبدلة: {report['total_replaced']}/{report['total_scanned']}")
    print(f"تم إنشاء التقرير: {REPORT_PATH}")
    if args.apply:
        print("تم تنفيذ الاستبدال بنجاح.")
    else:
        print("وضع فحص فقط. أعد التشغيل مع --apply للتنفيذ.")

if __name__ == "__main__":
    main()
