# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline -B
COPY src ./src
# Compile and package
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/investment-tracker-backend-1.0.0.jar app.jar

# Run as non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs
USER spring:spring


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
