@echo off
echo Building missing JAR files...

echo Building iccsoft_user...
cd "back_end_courrier\iccsoft_user"
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: iccsoft_user build failed!
    pause
    exit /b 1
)
cd ..\..

echo Building iccsoft_courrier...
cd "back_end_courrier\iccsoft_courrier"
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: iccsoft_courrier build failed!
    pause
    exit /b 1
)
cd ..\..

echo JAR files built successfully!
pause