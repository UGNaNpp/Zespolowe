FROM eclipse-temurin:23-alpine AS builder
WORKDIR /opt/app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Zcache'uj Maven dependencies do /root/.m2 (zostaje między buildami)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode dependency:go-offline

# Teraz kopiuj kod i buduj, też z cache'owaniem
COPY src src/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean install -DskipTests

FROM eclipse-temurin:23-jdk AS final
LABEL authors="Michał Kopczewski"
WORKDIR /opt/app
EXPOSE 8080

RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/* && ffmpeg -version

COPY --from=builder /opt/app/target/*.jar /opt/app/app.jar



ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
