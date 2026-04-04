FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . .
RUN chmod +x mvnw
RUN ./mvnw -DskipTests clean package \
    && find target -maxdepth 1 -type f -name "*.jar" ! -name "*original*.jar" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
