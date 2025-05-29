FROM eclipse-temurin:23-alpine AS builder
WORKDIR /opt/app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src src/

RUN ./mvnw dependency:go-offline -B

RUN ./mvnw clean install -DskipTests
#RUN ./mvnw clean install

FROM eclipse-temurin:23-alpine AS final
LABEL authors="Michał Kopczewski"
WORKDIR /opt/app
EXPOSE 8080

COPY --from=builder /opt/app/target/*.jar /opt/app/app.jar



ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
