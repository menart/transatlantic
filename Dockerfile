# Build stage (используем официальный multi-arch образ)
FROM maven:3.9.6-eclipse-temurin-21-alpine as builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage (официальный образ для всех архитектур)
FROM eclipse-temurin:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]