FROM openjdk:17-alpine
WORKDIR /app
COPY target/deconz.jar /app.jar
CMD ["java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]