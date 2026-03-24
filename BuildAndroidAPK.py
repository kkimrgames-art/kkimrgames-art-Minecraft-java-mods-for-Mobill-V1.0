import os
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path


ROOT = Path(__file__).resolve().parent
WORKSPACE = ROOT / "sources" / "setup" / "workspace_template"
APK_DEBUG = WORKSPACE / "target_android_webapk" / "build" / "outputs" / "apk" / "debug" / "target_android_webapk-debug.apk"
DESKTOP = Path.home() / "Desktop"


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


def run_cmd(cmd, cwd, env):
    result = subprocess.run(cmd, cwd=cwd, env=env)
    return result.returncode == 0


def ensure_web_build(env, java_cmd):
    classes_js = ROOT / "web-build" / "classes.js"
    assets_epk = ROOT / "web-build" / "assets.epk"
    if classes_js.is_file() and assets_epk.is_file():
        return True
    print("[INFO] جاري تجهيز web-build قبل بناء APK...")
    cmd = [
        java_cmd,
        "-cp",
        "buildtools/BuildTools.jar",
        "net.lax1dude.eaglercraft.v1_8.buildtools.gui.headless.CompileLatestClientHeadless",
        "-y",
        "buildtools_config.json",
    ]
    return run_cmd(cmd, ROOT, env)


def tune_mob_spawns(env):
    script = ROOT / "TuneMobSpawns.py"
    if not script.is_file():
        return True
    print("[INFO] تطبيق ضبط نسب ظهور المخلوقات...")
    if not run_cmd([sys.executable, str(script)], ROOT, env):
        print("[ERROR] فشل تطبيق ضبط نسب الظهور.")
        return False
    return True


def build_apk(env):
    gradlew = WORKSPACE / "gradlew.bat"
    if not gradlew.is_file():
        print("[ERROR] gradlew.bat غير موجود داخل workspace_template")
        return False
    cmd = [str(gradlew), "-PandroidOnly=true", ":target_android_webapk:assembleDebug"]
    print("[INFO] بدء بناء APK...")
    return run_cmd(cmd, WORKSPACE, env)


def copy_final_apk():
    if not APK_DEBUG.is_file():
        print("[ERROR] ملف APK النهائي غير موجود بعد البناء.")
        return False
    DESKTOP.mkdir(parents=True, exist_ok=True)
    stamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    target = DESKTOP / f"EaglercraftWebAPK_{stamp}.apk"
    shutil.copy2(APK_DEBUG, target)
    print(f"[OK] تم نسخ APK إلى: {target}")
    return True


def main():
    env, java_cmd = find_java_env()
    if not run_cmd([java_cmd, "-version"], ROOT, env):
        print("[ERROR] Java غير متاح.")
        sys.exit(1)
    if not ensure_web_build(env, java_cmd):
        print("[ERROR] فشل تجهيز web-build.")
        sys.exit(1)
    if not tune_mob_spawns(env):
        sys.exit(1)
    if not build_apk(env):
        print("[ERROR] فشل بناء APK.")
        sys.exit(1)
    if not copy_final_apk():
        sys.exit(1)


if __name__ == "__main__":
    main()
