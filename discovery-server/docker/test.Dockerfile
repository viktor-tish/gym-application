FROM eclipse-temurin:21-jdk AS tester

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw && \
    apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN ./mvnw dependency:go-offline

COPY src/ ./src
COPY config/ ./config
COPY docker/test-entrypoint.sh .

RUN chmod +x test-entrypoint.sh

ENTRYPOINT ["./test-entrypoint.sh"]
