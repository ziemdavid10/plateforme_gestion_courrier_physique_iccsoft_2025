FROM openjdk:17
#Construire une image de notre microservice en prenant pour base openjdk17
COPY target/*.jar registry-0.0.1-SNAPSHOT.jar
#Copie de tous les fichiers .jar dans le docker
ENTRYPOINT ["java", "-jar", "/registry-0.0.1-SNAPSHOT.jar"]
#Permets de lancer l'image
EXPOSE 8061
#Permets d'exposer l'image