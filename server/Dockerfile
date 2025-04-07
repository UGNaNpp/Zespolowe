FROM maven:3.9.9-eclipse-temurin-24-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:23-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y curl && apt-get clean

COPY --from=build /app/build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]