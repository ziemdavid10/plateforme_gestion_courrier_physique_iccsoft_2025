@echo off
echo Démarrage des services sans Docker...

echo Démarrage MySQL (assurez-vous qu'il soit installé)...
net start mysql80

echo Démarrage Registry (port 8761)...
start "Registry" cmd /k "cd back_end_courrier\registry && java -jar target\*.jar"

timeout /t 10

echo Démarrage Gateway (port 8080)...
start "Gateway" cmd /k "cd back_end_courrier\gateway && java -jar target\*.jar"

timeout /t 5

echo Démarrage User Service (port 8081)...
start "User-Service" cmd /k "cd back_end_courrier\iccsoft_user && java -jar target\*.jar"

echo Démarrage Courrier Service (port 8082)...
start "Courrier-Service" cmd /k "cd back_end_courrier\iccsoft_courrier && java -jar target\*.jar"

echo Démarrage Demo Service (port 8083)...
start "Demo-Service" cmd /k "cd back_end_courrier\demo && java -jar target\*.jar"

timeout /t 5

echo Démarrage Frontend...
start "Frontend" cmd /k "cd front_end_courrier && npm start"

echo Tous les services sont en cours de démarrage!
echo Frontend: http://localhost:4200
echo Gateway: http://localhost:8080
pause