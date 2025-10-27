@echo off
echo Fixing Docker connectivity issues...

echo Restarting Docker service...
net stop com.docker.service
net start com.docker.service

echo Clearing Docker cache...
docker system prune -f

echo Pulling MySQL image manually...
docker pull mysql:8.0

echo Docker fix completed. Try deploying again.