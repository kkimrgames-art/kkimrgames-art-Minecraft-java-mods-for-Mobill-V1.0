import argparse
import json
import shutil
from pathlib import Path


ROOT = Path(__file__).resolve().parent
DEFAULT_SOURCE = Path(r"C:\Users\Sidivall AI\Desktop\ماين كرافت جافا\Minecraft-Sounds-main\sounds")
TARGET_SOUNDS = ROOT / "sources" / "resources" / "assets" / "minecraft" / "sounds"
REPORT_PATH = ROOT / "sound_migration_report.json"


def to_nested_relative(flat_name: str):
    parts = [p for p in flat_name.split("_") if p]
    if len(parts) < 2:
        return Path(flat_name.lower().replace(" ", "_") + ".ogg")
    dirs = [p.lower() for p in parts[:-1]]
    file_name = parts[-1].lower().replace(" ", "_") + ".ogg"
    return Path(*dirs) / file_name


def clean_dir(target_dir: Path):
    removed = 0
    if target_dir.exists():
        shutil.rmtree(target_dir)
    target_dir.mkdir(parents=True, exist_ok=True)
    return removed


def run(source_dir: Path, apply_changes: bool):
    if not source_dir.is_dir():
        raise FileNotFoundError("مجلد المصدر غير موجود")

    source_files = sorted(source_dir.glob("*.ogg"))
    report = {
        "source_dir": str(source_dir),
        "target_dir": str(TARGET_SOUNDS),
        "apply_changes": apply_changes,
        "source_files": len(source_files),
        "flat_written": 0,
        "nested_written": 0,
        "conflicts": 0,
        "samples": [],
    }

    if not apply_changes:
        for src in source_files[:20]:
            rel = to_nested_relative(src.stem)
            report["samples"].append({
                "src": src.name,
                "flat": src.name,
                "nested": str(rel).replace("\\", "/"),
            })
        REPORT_PATH.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        return report

    clean_dir(TARGET_SOUNDS)
    seen_nested = set()

    for src in source_files:
        flat_target = TARGET_SOUNDS / src.name
        flat_target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, flat_target)
        report["flat_written"] += 1

        nested_rel = to_nested_relative(src.stem)
        nested_target = TARGET_SOUNDS / nested_rel
        nested_key = str(nested_rel).lower().replace("\\", "/")
        if nested_key in seen_nested:
            report["conflicts"] += 1
            continue
        seen_nested.add(nested_key)
        nested_target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, nested_target)
        report["nested_written"] += 1

        if len(report["samples"]) < 30:
            report["samples"].append({
                "src": src.name,
                "flat": src.name,
                "nested": nested_key,
            })

    REPORT_PATH.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    return report


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", default=str(DEFAULT_SOURCE))
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()

    report = run(Path(args.source), args.apply)
    print(f"المصدر: {report['source_dir']}")
    print(f"عدد ملفات المصدر: {report['source_files']}")
    print(f"نسخ مباشر (flat): {report['flat_written']}")
    print(f"نسخ متداخل (nested): {report['nested_written']}")
    print(f"تعارض أسماء nested: {report['conflicts']}")
    print(f"التقرير: {REPORT_PATH}")
    if args.apply:
        print("تم استبدال الأصوات بنجاح.")
    else:
        print("وضع فحص فقط. أعد التشغيل مع --apply.")


if __name__ == "__main__":
    main()
