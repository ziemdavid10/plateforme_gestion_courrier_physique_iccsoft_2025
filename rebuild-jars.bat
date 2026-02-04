@echo off
echo Nettoyage et reconstruction des JAR files...

cd back_end_courrier

echo Nettoyage des anciens builds...
cd registry
if exist target rmdir /s /q target
cd ..

cd gateway
if exist target rmdir /s /q target
cd ..

cd iccsoft_user
if exist target rmdir /s /q target
cd ..

cd iccsoft_courrier
if exist target rmdir /s /q target
cd ..

cd demo
if exist target rmdir /s /q target
cd ..

echo Reconstruction des JAR files...
cd registry
echo Construction registry...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Erreur lors de la construction de registry
    pause
    exit /b 1
)
cd ..

cd gateway
echo Construction gateway...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Erreur lors de la construction de gateway
    pause
    exit /b 1
)
cd ..

cd iccsoft_user
echo Construction iccsoft_user...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Erreur lors de la construction de iccsoft_user
    pause
    exit /b 1
)
cd ..

cd iccsoft_courrier
echo Construction iccsoft_courrier...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Erreur lors de la construction de iccsoft_courrier
    pause
    exit /b 1
)
cd ..

cd demo
echo Construction demo...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Erreur lors de la construction de demo
    pause
    exit /b 1
)
cd ..

echo Tous les JAR files ont été reconstruits avec succès!
pause