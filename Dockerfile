# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Skip tests to speed up the build for now (optional but recommended for first docker run)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar /app/rentit.jar

# Expose port 8080 (standard Spring Boot port)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "rentit.jar"]