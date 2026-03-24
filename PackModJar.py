import argparse
import re
import sys
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parent


def _clean_toml_value(value: str) -> str:
    value = value.strip()
    if len(value) >= 2 and value[0] == '"' and value[-1] == '"':
        return value[1:-1]
    if len(value) >= 2 and value[0] == "'" and value[-1] == "'":
        return value[1:-1]
    return value


def _parse_mod_identity(mods_toml_path: Path):
    if not mods_toml_path.is_file():
        return None, None, None
    mod_id = None
    mod_version = None
    mc_version = None
    in_mods = False
    in_dep = False
    dep_mod_id = None
    try:
        lines = mods_toml_path.read_text(encoding="utf-8", errors="replace").replace("\r", "").split("\n")
    except Exception:
        return None, None, None
    for raw in lines:
        line = raw.split("#", 1)[0].strip()
        if not line:
            continue
        if line.startswith("[[mods]]"):
            in_mods = True
            in_dep = False
            dep_mod_id = None
            continue
        if line.startswith("[[dependencies."):
            in_mods = False
            in_dep = True
            dep_mod_id = None
            continue
        if "=" not in line:
            continue
        key, value = line.split("=", 1)
        key = key.strip()
        value = _clean_toml_value(value.strip())
        if in_mods:
            if key == "modId" and mod_id is None:
                mod_id = value
            elif key == "version" and mod_version is None:
                mod_version = value
        elif in_dep:
            if key == "modId":
                dep_mod_id = value.lower()
            elif key == "versionRange" and dep_mod_id == "minecraft":
                nums = re.findall(r"\d+(?:\.\d+)*", value)
                if nums:
                    mc_version = nums[0]
    return mod_id, mod_version, mc_version


def _guess_output_name(source_dir: Path) -> str:
    mod_id, mod_version, mc_version = _parse_mod_identity(source_dir / "META-INF" / "mods.toml")
    mod_id = mod_id or "mod"
    mod_version = mod_version or "dev"
    mc_version = mc_version or "1.20.1"
    return f"{mod_id}-forge-{mc_version}-{mod_version}.jar"


def _collect_files(source_dir: Path, output_path: Path):
    files = []
    output_resolved = output_path.resolve()
    for path in sorted(source_dir.rglob("*")):
        if not path.is_file():
            continue
        if path.resolve() == output_resolved:
            continue
        lower = path.name.lower()
        if lower.endswith(".jar") or lower.endswith(".zip"):
            continue
        files.append(path)
    return files


def pack_mod_jar(source_dir: Path, output_path: Path, verbose: bool) -> int:
    if not source_dir.is_dir():
        print(f"[ERROR] مجلد المصدر غير موجود: {source_dir}")
        return 1
    output_path.parent.mkdir(parents=True, exist_ok=True)
    files = _collect_files(source_dir, output_path)
    if not files:
        print("[ERROR] لا توجد ملفات قابلة للضغط داخل المود")
        return 1
    with zipfile.ZipFile(output_path, "w", compression=zipfile.ZIP_DEFLATED) as jar:
        for file_path in files:
            arcname = file_path.relative_to(source_dir).as_posix()
            jar.write(file_path, arcname)
            if verbose:
                print(f"[ADD] {arcname}")
    size_kb = output_path.stat().st_size / 1024.0
    print(f"[SUCCESS] تم إنشاء المود: {output_path}")
    print(f"[INFO] عدد الملفات: {len(files)} | الحجم: {size_kb:.1f} KB")
    return 0


def main():
    parser = argparse.ArgumentParser(description="تجميع ملفات المود الحالية في ملف JAR للاختبار")
    parser.add_argument("--source", default="mods", help="مجلد ملفات المود (افتراضي: mods)")
    parser.add_argument("--output", default=None, help="مسار ملف jar الناتج")
    parser.add_argument("--verbose", action="store_true", help="عرض كل ملف أثناء التجميع")
    args = parser.parse_args()

    source_dir = Path(args.source)
    if not source_dir.is_absolute():
        source_dir = (ROOT / source_dir).resolve()

    if args.output:
        output_path = Path(args.output)
        if not output_path.is_absolute():
            output_path = (ROOT / output_path).resolve()
    else:
        output_path = source_dir / _guess_output_name(source_dir)

    code = pack_mod_jar(source_dir, output_path, args.verbose)
    sys.exit(code)


if __name__ == "__main__":
    main()
