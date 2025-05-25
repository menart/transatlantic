# Build stage
FROM mvkvl/maven:jdk-21-alpine as builder
WORKDIR /app
COPY ./pom.xml .
RUN mvn -B dependency:go-offline
COPY ./src ./src
RUN mvn -B package -DskipTests

# Runtime stage
FROM alpine/java:21-jdk
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]