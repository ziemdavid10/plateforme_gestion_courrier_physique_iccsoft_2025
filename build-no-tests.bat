@echo off
echo Construction sans tests...

cd back_end_courrier

echo Construction registry...
cd registry
call mvn clean package -DskipTests -Dmaven.test.skip=true
cd ..

echo Construction gateway...
cd gateway  
call mvn clean package -DskipTests -Dmaven.test.skip=true
cd ..

echo Construction iccsoft_user...
cd iccsoft_user
call mvn clean package -DskipTests -Dmaven.test.skip=true
cd ..

echo Construction iccsoft_courrier...
cd iccsoft_courrier
call mvn clean package -DskipTests -Dmaven.test.skip=true
cd ..

echo Construction demo...
cd demo
call mvn clean package -DskipTests -Dmaven.test.skip=true
cd ..

echo Build terminé sans tests!
pause