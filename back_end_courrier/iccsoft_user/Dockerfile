# Construire une image de notre microservice en prenant pour base openjdk17, Permet d'indiquer sur quelle image de base on veut construire une image personnalisée
FROM openjdk:17
# Copie de tous les fichiers .jar dans le docker, Copier tous les fichiers Jar qui sont dans le repertoire indiqué vers le conteneur
COPY target/*.jar iccsoft_user-0.0.1-SNAPSHOT.jar
# # Permets de lancer l'image, Commande qui sera lancée lorsqu'on va démarrer le conteneur
ENTRYPOINT ["java", "-jar", "/iccsoft_user-0.0.1-SNAPSHOT.jar"]
# Permets d'exposer l'image sur le port 8082
EXPOSE 8082