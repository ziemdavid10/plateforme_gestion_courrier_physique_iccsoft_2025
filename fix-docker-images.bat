@echo off
echo Pulling required Docker images manually...

echo Pulling OpenJDK 17 (alternative)...
docker pull eclipse-temurin:17-jre
if errorlevel 1 (
    echo Trying Microsoft registry...
    docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu
    docker tag mcr.microsoft.com/openjdk/jdk:17-ubuntu openjdk:17-jdk-slim
)

echo Pulling Node 18...
docker pull node:18-alpine
if errorlevel 1 echo Failed to pull node:18-alpine

echo Pulling Nginx...
docker pull nginx:alpine
if errorlevel 1 echo Failed to pull nginx:alpine

echo Pulling MySQL...
docker pull mysql:8.0
if errorlevel 1 echo Failed to pull mysql:8.0

echo Done! Now try running deploy-local.bat
pause