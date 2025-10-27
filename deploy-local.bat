@echo off
echo Starting local deployment (using local MySQL)...

echo Checking if MySQL is running locally...
netstat -an | find "3306" >nul
if errorlevel 1 (
    echo ERROR: MySQL is not running on port 3306
    echo Please start your local MySQL server first
    echo Database required: courrier_db
    echo User: courrier_user, Password: courrier_pass
    pause
    exit /b 1
)

echo MySQL detected on port 3306

echo Building backend services...
call build-backend.bat
if errorlevel 1 (
    echo Backend build failed!
    pause
    exit /b 1
)

echo Starting Docker containers with local MySQL...
docker-compose -f docker-compose-local.yml up --build -d

if errorlevel 1 (
    echo Docker deployment failed!
    pause
    exit /b 1
)

echo Deployment successful!
echo Services available at:
echo - Frontend: http://localhost
echo - Gateway: http://localhost:8080
echo - Registry: http://localhost:8761

pause