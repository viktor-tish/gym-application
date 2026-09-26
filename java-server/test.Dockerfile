FROM eclipse-temurin:17-jdk AS tester

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw dependency:go-offline

COPY src/ ./src
COPY config/ ./config

RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw clean test
