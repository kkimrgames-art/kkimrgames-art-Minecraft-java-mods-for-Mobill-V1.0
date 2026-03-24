@echo off
setlocal
cd /d "%~dp0"

set "PORT=8090"
set "JDK_DIR="

for /d %%D in ("local-tools\jdk-*") do (
    if exist "%%~fD\bin\java.exe" (
        set "JDK_DIR=%%~fD"
        goto :jdk_found
    )
)

:jdk_found
if defined JDK_DIR (
    set "JAVA_HOME=%JDK_DIR%"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java غير متاح. شغل اعداد Java اولا.
    pause
    exit /b 1
)

if not exist "mcp918\mcp918.zip" (
    echo [ERROR] الملف مفقود: mcp918\mcp918.zip
    pause
    exit /b 1
)

if not exist "mcp918\1.8.8.jar" (
    echo [ERROR] الملف مفقود: mcp918\1.8.8.jar
    pause
    exit /b 1
)

if not exist "mcp918\1.8.json" (
    echo [ERROR] الملف مفقود: mcp918\1.8.json
    pause
    exit /b 1
)

if not exist "web-build\classes.js" goto :build_now
if not exist "web-build\assets.epk" goto :build_now
goto :run_now

:build_now
echo [INFO] جاري تجهيز نسخة الويب...
java -Xmx8G -cp "buildtools/BuildTools.jar" net.lax1dude.eaglercraft.v1_8.buildtools.gui.headless.CompileLatestClientHeadless -y buildtools_config.json
if errorlevel 1 (
    echo [ERROR] فشل تجهيز نسخة الويب.
    pause
    exit /b 1
)

:run_now
echo [INFO] تشغيل الخادم المحلي على المنفذ %PORT%
start "" "http://127.0.0.1:%PORT%/"
python -m http.server %PORT% --directory "web-build"

endlocal
