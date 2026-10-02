@echo off
echo ====================================================================
echo  Launching Satellite Mission and Orbit Data Management System...
echo ====================================================================

java -cp "bin;lib/*;src/resources" Main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
