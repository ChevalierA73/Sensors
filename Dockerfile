FROM openjdk:11.0.6-jre-slim-buster
WORKDIR /app
COPY target/deconz.jar /app.jar
CMD ["java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]