FROM openjdk:17-jdk
WORKDIR /app
COPY target/registry-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]