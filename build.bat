@echo off
echo ====================================================================
echo  Building Satellite Mission and Orbit Data Management System...
echo ====================================================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac -encoding UTF-8 -cp "lib/*" -d bin src/model/*.java src/dao/*.java src/database/*.java src/util/*.java src/service/*.java src/gui/theme/*.java src/gui/components/*.java src/gui/*.java src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [BUILD SUCCESS] All classes compiled successfully into bin/ directory!
    echo Copying assets and resources to bin...
    xcopy /s /y /i src\resources bin\ >nul 2>&1
    echo Done.
) else (
    echo.
    echo [BUILD ERROR] Compilation failed. Please check the errors above.
    pause
)
