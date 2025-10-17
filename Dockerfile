FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
COPY --from=builder /build/${JAR_FILE} /app/demo.jar

ENTRYPOINT ["java", "-jar", "/app/demo.jar"]