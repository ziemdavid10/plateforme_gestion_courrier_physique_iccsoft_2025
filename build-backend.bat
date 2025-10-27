@echo off
echo Building all microservices...

cd back_end_courrier\registry
mvn clean package -DskipTests
cd ..\..

cd back_end_courrier\gateway
mvn clean package -DskipTests
cd ..\..

cd back_end_courrier\iccsoft_user
mvn clean package -DskipTests
cd ..\..

cd back_end_courrier\iccsoft_courrier
mvn clean package -DskipTests
cd ..\..

cd back_end_courrier\demo
mvn clean package -DskipTests
cd ..\..

echo All microservices built successfully!