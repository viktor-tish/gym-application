FROM eclipse-temurin:17-jdk AS builder

WORKDIR /build

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw dependency:go-offline

COPY src/ ./src
COPY config/ ./config

RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /build/target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
