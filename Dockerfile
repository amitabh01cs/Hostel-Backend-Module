 # --- Build Stage ---
FROM maven:3.8.4-openjdk-17 As build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (cached if unchanged)
RUN ./mvnw dependency:go-offline

# Copy rest of the project source
COPY src ./src

# Build the application JAR, skip tests for faster build
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# --- Run Stage ---
FROM openjdk:17-jdk-slim

WORKDIR /app

# Expose the port your Spring Boot app runs on (change if needed)
EXPOSE 8085

# Copy the built JAR from build stage
COPY --from=build /app/target/testfrontendbackenddb-0.0.1-SNAPSHOT.jar .

# Run the application
ENTRYPOINT ["java", "-jar", "/app/testfrontendbackenddb-0.0.1-SNAPSHOT.jar"]
  
  
