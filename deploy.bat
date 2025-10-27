@echo off
echo Checking Docker connectivity...
docker version
if %errorlevel% neq 0 (
    echo Docker is not running. Please start Docker Desktop.
    pause
    exit /b 1
)

echo Building backend...
call build-backend.bat
if %errorlevel% neq 0 (
    echo Backend build failed!
    pause
    exit /b 1
)

echo Starting Docker containers...
docker-compose up --build -d
if %errorlevel% neq 0 (
    echo Docker deployment failed!
    echo Trying alternative approach...
    docker-compose up --build
    pause
    exit /b 1
)

echo Waiting for services to start...
timeout /t 30 /nobreak

echo Checking service status...
docker-compose ps

echo Deployment completed!
echo Frontend: http://localhost
echo Backend Gateway: http://localhost:8080
echo Eureka Registry: http://localhost:8761