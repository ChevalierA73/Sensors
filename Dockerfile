FROM openjdk:11.0.15-jdk-slim
WORKDIR /app
COPY target/deconz.jar /app.jar
CMD ["java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]