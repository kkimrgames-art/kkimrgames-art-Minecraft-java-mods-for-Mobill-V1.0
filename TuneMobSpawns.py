import json
from pathlib import Path


ROOT = Path(__file__).resolve().parent
REPORT_PATH = ROOT / "spawn_tuning_report.json"
TARGETS = [
    ROOT / "web-build" / "classes.js",
    ROOT / "sources" / "setup" / "workspace_template" / "target_android_webapk" / "build" / "intermediates" / "assets" / "debug" / "mergeDebugAssets" / "classes.js",
]


REPLACEMENTS = [
    ("ABM(E(AGX),10,3,3)", "ABM(E(AGX),0,2,3)", "base.rabbit.disable_global"),
    ("ABM(E(R9),100,4,4)", "ABM(E(R9),95,4,4)", "base.zombie.weight_95"),
]


def patch_one(path: Path):
    if not path.is_file():
        return {"file": str(path), "exists": False, "changed": 0, "details": []}
    txt = path.read_text(encoding="utf-8")
    changed = 0
    details = []
    for old, new, key in REPLACEMENTS:
        count = txt.count(old)
        if count > 0:
            txt = txt.replace(old, new)
            changed += count
            details.append({"rule": key, "count": count})
        else:
            details.append({"rule": key, "count": 0})
    if changed > 0:
        path.write_text(txt, encoding="utf-8")
    return {"file": str(path), "exists": True, "changed": changed, "details": details}


def run():
    results = [patch_one(p) for p in TARGETS]
    total = sum(r["changed"] for r in results if r["exists"])
    report = {"total_replacements": total, "targets": results}
    REPORT_PATH.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    return report


if __name__ == "__main__":
    report = run()
    print(f"إجمالي التعديلات: {report['total_replacements']}")
    print(f"التقرير: {REPORT_PATH}")
