@echo off
echo Trying alternative Docker registries...

echo Pulling OpenJDK 17 from Microsoft...
docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu
docker tag mcr.microsoft.com/openjdk/jdk:17-ubuntu openjdk:17-jdk-slim

echo Pulling Node 18 from alternative...
docker pull registry.hub.docker.com/library/node:18-alpine
docker tag registry.hub.docker.com/library/node:18-alpine node:18-alpine

echo Pulling Nginx from alternative...
docker pull registry.hub.docker.com/library/nginx:alpine
docker tag registry.hub.docker.com/library/nginx:alpine nginx:alpine

echo Images pulled successfully!
pause