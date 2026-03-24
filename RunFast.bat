@echo off
echo ========================================
echo    تشغيل سريع للعبة ماين كرافت
echo ========================================
echo.

:: التحقق من وجود نسخة مبنية
if exist "web-build\classes.js" if exist "web-build\assets.epk" (
    echo [INFO] تم العثور على نسخة مبنية، سيتم تجاوز عملية البناء...
    echo.
    echo للتشغيل مع إعادة بناء كامل، استخدم RunWebGame.bat
    echo.
    
    :: تشغيل اللعبة مع تجاوز البناء والمودات والسباونز
    python RunWebGame.py --skip-build --skip-mods --skip-spawns
) else (
    echo [WARNING] لا توجد نسخة مبنية، سيتم البناء لأول مرة...
    echo.
    
    :: تشغيل اللعبة بشكل عادي لأول مرة
    python RunWebGame.py
)

if errorlevel 1 (
    echo.
    echo [ERROR] حدث خطأ أثناء تشغيل اللعبة
    pause
)