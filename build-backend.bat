@echo off
echo Building all microservices...

echo Building registry...
cd "back_end_courrier\registry"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Registry build failed!
    pause
    exit /b 1
)
cd ..\..

echo Building gateway...
cd "back_end_courrier\gateway"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Gateway build failed!
    pause
    exit /b 1
)
cd ..\..

echo Building iccsoft_user...
cd "back_end_courrier\iccsoft_user"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: iccsoft_user build failed!
    pause
    exit /b 1
)
cd ..\..

echo Building iccsoft_courrier...
cd "back_end_courrier\iccsoft_courrier"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: iccsoft_courrier build failed!
    pause
    exit /b 1
)
cd ..\..

echo Building demo...
cd "back_end_courrier\demo"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Demo build failed!
    pause
    exit /b 1
)
cd ..\..

echo All microservices built successfully!
pause