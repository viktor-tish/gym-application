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

FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY --from=builder /build/target/app.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
