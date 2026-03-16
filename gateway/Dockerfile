# Stage 1: Build the Gateway application
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Run as non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs
USER spring:spring

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
