# Construction de l'image à partir de l'image officielle OpenJDK 17
FROM openjdk:17
# Copie de tous les fichiers .JAR de l'application dans le conteneur
COPY target/*.jar demo-0.0.1-SNAPSHOT.jar
# Permet de lancer l'image
ENTRYPOINT ["java","-jar","/demo-0.0.1-SNAPSHOT.jar"]
# Expose le port 8081 pour accéder à l'application
EXPOSE 8081
# Commande pour construire l'image Docker
# docker build -t demo .
# Commande pour lancer le conteneur Docker
# docker run -p 8081:8081 demo
# Commande pour lancer une nouvelle instance de l'application sur un autre port
# docker run -e "SERVER_PORT=8082" -p 8082:8082 demo
# Commande pour lancer une nouvelle instance de l'application sur un autre port avec un nom différent
# docker run -e "SERVER_PORT=8083" -e "SPRING_APPLICATION_NAME=demo2" -p 8083:8083 demo
# Commande pour lancer une nouvelle instance de l'application sur un autre port avec un nom différent et se connecter à un autre serveur Eureka
# docker run -e "SERVER_PORT=8084" -e "SPRING_APPLICATION_NAME=demo3" -e "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://root:ziem@localhost:8762/eureka/" -p 8084:8084 demo