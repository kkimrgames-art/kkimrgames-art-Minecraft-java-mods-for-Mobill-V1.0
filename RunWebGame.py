import argparse
import http.server
import os
import socketserver
import subprocess
import sys
import webbrowser
import zipfile
from functools import partial
from pathlib import Path


PORT = 8090
ROOT = Path(__file__).resolve().parent


def find_java_env():
    env = os.environ.copy()
    candidates = sorted((ROOT / "local-tools").glob("jdk-*"))
    for candidate in candidates:
        java_exe = candidate / "bin" / "java.exe"
        if java_exe.is_file():
            env["JAVA_HOME"] = str(candidate)
            env["PATH"] = str(candidate / "bin") + os.pathsep + env.get("PATH", "")
            return env, str(java_exe)
    java_home = env.get("JAVA_HOME")
    if java_home:
        java_exe = Path(java_home) / "bin" / "java.exe"
        if java_exe.is_file():
            return env, str(java_exe)
    return env, "java"


def run_checked(cmd, env):
    print(f"[CMD] {' '.join(str(x) for x in cmd)}")
    result = subprocess.run(cmd, cwd=ROOT, env=env, capture_output=True, text=True, encoding='utf-8', errors='replace')
    print(result.stdout)
    if result.returncode != 0:
        print(result.stderr)
        # Copy to clipboard using PowerShell (Win + V)
        error_text = (result.stdout + "\n" + result.stderr).strip()
        if error_text:
            lines = error_text.splitlines()
            # Take last 100 lines to keep it manageable
            error_summary = "\n".join(lines[-100:])
            try:
                # Use powershell to set clipboard
                subprocess.run(["powershell", "-NoProfile", "-Command", "$input | Set-Clipboard"], input=error_summary, text=True, encoding='utf-8')
                print("\n[INFO] 📋 تم نسخ الأخطاء تلقائياً إلى الحافظة! يمكنك الآن لصقها (Win + V)")
            except Exception as e:
                print(f"[DEBUG] Clipboard error: {e}")
    return result.returncode == 0


def _find_javac(java_cmd, env):
    java_path = Path(java_cmd)
    if java_path.name.lower() == "java.exe":
        javac = java_path.parent / "javac.exe"
        if javac.is_file():
            return str(javac)
    java_home = env.get("JAVA_HOME")
    if java_home:
        javac = Path(java_home) / "bin" / "javac.exe"
        if javac.is_file():
            return str(javac)
    return "javac"


def _build_buildtools_classpath(env, java_cmd):
    buildtools_src = ROOT / "buildtools" / "src" / "main" / "java"
    buildtools_res = ROOT / "buildtools" / "src" / "main" / "resources"
    deps_dir = ROOT / "buildtools" / "deps"
    out_dir = ROOT / "buildtools" / "__runclasses"
    stamp_file = out_dir / "__stamp.txt"

    sources = sorted(buildtools_src.rglob("*.java"))
    if not sources:
        return "buildtools/BuildTools.jar"

    src_mtime = max(p.stat().st_mtime for p in sources)
    deps = sorted(deps_dir.glob("*.jar"))
    deps_mtime = max((p.stat().st_mtime for p in deps), default=0)
    desired_stamp = f"src={src_mtime}\ndeps={deps_mtime}\n"

    if out_dir.is_dir() and stamp_file.is_file():
        try:
            if stamp_file.read_text(encoding="utf-8") == desired_stamp:
                cp_parts = [str(out_dir), str(buildtools_res)] + [str(p) for p in deps]
                return os.pathsep.join(cp_parts)
        except Exception:
            pass

    out_dir.mkdir(parents=True, exist_ok=True)
    javac_cmd = _find_javac(java_cmd, env)
    cp = os.pathsep.join([p.as_posix() for p in deps])
    
    argfile = out_dir / "__sources.txt"
    with argfile.open("w", encoding="utf-8") as f:
        f.write(f"--release 8\n")
        f.write(f"-encoding UTF-8\n")
        f.write(f"-cp \"{cp}\"\n")
        f.write(f"-d \"{out_dir.as_posix()}\"\n")
        for src in sources:
            f.write(f"\"{src.as_posix()}\"\n")

    print("[INFO] تجهيز BuildTools محلياً لتسريع البناء...")
    cmd = [
        javac_cmd,
        "@" + str(argfile),
    ]
    if not run_checked(cmd, env):
        print("[WARNING] فشل تجهيز BuildTools محلياً، سيتم استخدام BuildTools.jar")
        return "buildtools/BuildTools.jar"

    try:
        stamp_file.write_text(desired_stamp, encoding="utf-8")
    except Exception:
        pass

    cp_parts = [str(out_dir), str(buildtools_res)] + [str(p) for p in deps]
    return os.pathsep.join(cp_parts)


def ensure_requirements(env, java_cmd):
    if not run_checked([java_cmd, "-version"], env):
        print("[ERROR] Java غير متاح. شغل اعداد Java اولا.")
        return False
    required = [
        ROOT / "mcp918" / "mcp918.zip",
        ROOT / "mcp918" / "1.8.8.jar",
        ROOT / "mcp918" / "1.8.json",
    ]
    for file_path in required:
        if not file_path.is_file():
            print(f"[ERROR] الملف مفقود: {file_path.relative_to(ROOT)}")
            return False
    return True


def ensure_web_build(env, java_cmd):
    classes_js = ROOT / "web-build" / "classes.js"
    assets_epk = ROOT / "web-build" / "assets.epk"
    if classes_js.is_file() and assets_epk.is_file():
        watched = [
            ROOT / "sources" / "main" / "java" / "net" / "lax1dude" / "eaglercraft" / "v1_8" / "minecraft" / "ModManager.java",
            ROOT / "sources" / "main" / "java" / "net" / "lax1dude" / "eaglercraft" / "v1_8" / "minecraft" / "GuiScreenModManagement.java",
            ROOT / "sources" / "main" / "java" / "net" / "lax1dude" / "eaglercraft" / "v1_8" / "minecraft" / "GuiScreenModLogs.java",
            ROOT / "sources" / "main" / "java" / "net" / "lax1dude" / "eaglercraft" / "v1_8" / "sp" / "gui" / "GuiScreenBackupWorldSelection.java",
        ]
        try:
            build_time = min(classes_js.stat().st_mtime, assets_epk.stat().st_mtime)
            src_time = max(p.stat().st_mtime for p in watched if p.is_file())
            if src_time <= build_time:
                return True
        except Exception:
            return True
        print("[INFO] تم اكتشاف تغييرات جديدة في ملفات اللعبة، سيتم إعادة بناء نسخة الويب...")
    print("[INFO] جاري تجهيز نسخة الويب...")
    buildtools_cp = _build_buildtools_classpath(env, java_cmd)
    cmd = [
        java_cmd,
        "-Xmx8G",
        "-cp",
        buildtools_cp,
        "net.lax1dude.eaglercraft.v1_8.buildtools.gui.headless.CompileLatestClientHeadless",
        "-y",
        "buildtools_config.json",
    ]
    if not run_checked(cmd, env):
        print("[ERROR] فشل تجهيز نسخة الويب.")
        return False
    return True


def tune_mob_spawns(env):
    script = ROOT / "TuneMobSpawns.py"
    if not script.is_file():
        return True
    print("[INFO] تطبيق ضبط نسب ظهور المخلوقات...")
    if not run_checked([sys.executable, str(script)], env):
        print("[ERROR] فشل تطبيق ضبط نسب الظهور.")
        return False
    return True


def _clean_toml_value(value):
    value = value.strip()
    if value.startswith('"') and value.endswith('"') and len(value) >= 2:
        return value[1:-1]
    if value.startswith("'") and value.endswith("'") and len(value) >= 2:
        return value[1:-1]
    return value


def _parse_mods_toml(text):
    info = {"modLoader": None, "loaderVersion": None, "modId": None, "version": None, "dependencies": []}
    in_mod = False
    in_dep = False
    current_dep = None
    for raw_line in text.replace("\r", "").split("\n"):
        line = raw_line.split("#", 1)[0].strip()
        if not line:
            continue
        if line.startswith("[[mods]]"):
            in_mod = True
            in_dep = False
            current_dep = None
            continue
        if line.startswith("[[dependencies."):
            in_mod = False
            in_dep = True
            current_dep = {"modId": None, "mandatory": False, "versionRange": None}
            info["dependencies"].append(current_dep)
            continue
        if "=" not in line:
            continue
        key, value = [x.strip() for x in line.split("=", 1)]
        value = _clean_toml_value(value)
        if in_mod:
            if key == "modId":
                info["modId"] = value
            elif key == "version":
                info["version"] = value
        elif in_dep and current_dep is not None:
            if key == "modId":
                current_dep["modId"] = value
            elif key == "mandatory":
                current_dep["mandatory"] = value.lower() == "true"
            elif key == "versionRange":
                current_dep["versionRange"] = value
        else:
            if key == "modLoader":
                info["modLoader"] = value
            elif key == "loaderVersion":
                info["loaderVersion"] = value
    return info


def inspect_mods_folder():
    mods_dir = ROOT / "mods"
    if not mods_dir.is_dir():
        print(f"[WARNING] مجلد mods غير موجود: {mods_dir}")
        return True
    jar_files = sorted(mods_dir.glob("*.jar"))
    if not jar_files:
        print("[WARNING] لا يوجد مودات داخل مجلد mods")
        return True
    print(f"[INFO] فحص مودات Forge داخل: {mods_dir}")
    for jar_path in jar_files:
        try:
            with zipfile.ZipFile(jar_path, "r") as zf:
                names = set(zf.namelist())
                if "META-INF/mods.toml" not in names:
                    print(f"[WARNING] {jar_path.name}: لا يحتوي META-INF/mods.toml")
                    continue
                mods_toml = zf.read("META-INF/mods.toml").decode("utf-8", errors="replace")
                info = _parse_mods_toml(mods_toml)
                dep_text = ", ".join([
                    f'{d.get("modId")}:{d.get("versionRange")}'
                    for d in info["dependencies"]
                    if d.get("mandatory")
                ])
                print(f'[INFO] {jar_path.name} -> modId={info.get("modId")} version={info.get("version")} loader={info.get("modLoader")} deps=[{dep_text}]')
                if info.get("modLoader") != "javafml":
                    print(f"[WARNING] {jar_path.name}: modLoader غير مدعوم ({info.get('modLoader')})")
                has_mixins = any(name.endswith(".mixins.json") for name in names)
                if has_mixins:
                    print(f"[INFO] {jar_path.name}: يحتوي mixins ويحتاج طبقة ترجمة")
        except Exception as exc:
            print(f"[ERROR] فشل فحص المود {jar_path.name}: {exc}")
            return False
    return True


def prepare_test_world_mods(env):
    script = ROOT / "ManageTestWorld.py"
    if not script.is_file():
        return True
    print("[INFO] مزامنة مودات الاختبار داخل web-build/test-worlds...")
    return run_checked([sys.executable, str(script)], env)


def run_server(world_name=None):
    web_dir = ROOT / "web-build"
    handler = partial(http.server.SimpleHTTPRequestHandler, directory=str(web_dir))
    with socketserver.TCPServer(("127.0.0.1", PORT), handler) as httpd:
        url = f"http://127.0.0.1:{PORT}/"
        if world_name:
            url += f"?world={world_name}"
        print(f"[INFO] تشغيل الخادم المحلي على المنفذ {PORT}")
        print(f"[INFO] رابط الدخول المباشر: {url}")
        webbrowser.open(url)
        httpd.serve_forever()


def parse_args():
    parser = argparse.ArgumentParser(description="تشغيل لعبة ماين كرافت على المتصفح")
    parser.add_argument(
        "--build",
        action="store_true",
        help="إجبار إعادة بناء نسخة الويب حتى لو كانت موجودة (قد يأخذ وقت)"
    )
    parser.add_argument(
        "--skip-build",
        action="store_true",
        help="تجاوز عملية البناء إذا كانت النسخة المبنية موجودة بالفعل"
    )
    parser.add_argument(
        "--skip-mods",
        action="store_true",
        help="تخطي فحص المودات ومزامنتها"
    )
    parser.add_argument(
        "--skip-spawns",
        action="store_true",
        help="تخطي ضبط نسب ظهور المخلوقات"
    )
    parser.add_argument(
        "--port",
        type=int,
        default=8090,
        help="المنفذ الذي سيعمل عليه الخادم (افتراضي: 8090)"
    )
    parser.add_argument(
        "--world",
        type=str,
        default=None,
        help="اسم العالم للدخول إليه مباشرة (مثال: mods)"
    )
    return parser.parse_args()


def main():
    args = parse_args()
    
    # تحديث المنفذ إذا تم تحديده
    global PORT
    PORT = args.port
    
    env, java_cmd = find_java_env()
    if not ensure_requirements(env, java_cmd):
        sys.exit(1)
    
    if not args.skip_mods:
        if not inspect_mods_folder():
            sys.exit(1)
    
    classes_js = ROOT / "web-build" / "classes.js"
    assets_epk = ROOT / "web-build" / "assets.epk"

    has_build = classes_js.is_file() and assets_epk.is_file()
    if args.skip_build:
        if has_build:
            print("[INFO] --skip-build مفعل: سيتم تشغيل النسخة المبنية الحالية بدون فحص إعادة البناء")
        else:
            print("[WARNING] خيار --skip-build مفعل ولكن لا توجد نسخة مبنية، سيتم البناء...")
            if not ensure_web_build(env, java_cmd):
                sys.exit(1)
    else:
        if not ensure_web_build(env, java_cmd):
            sys.exit(1)
    
    if not args.skip_mods:
        if not prepare_test_world_mods(env):
            print("[ERROR] فشل مزامنة مودات الاختبار.")
            sys.exit(1)
    
    if not args.skip_spawns:
        if not tune_mob_spawns(env):
            sys.exit(1)
    
    try:
        run_server(args.world)
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
