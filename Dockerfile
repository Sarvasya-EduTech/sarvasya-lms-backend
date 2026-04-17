# Stage 1: Build the application
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app

# Copy the wrapper, pom.xml, and source code
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src

# Package the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:26-jre
WORKDIR /app

# Expose the application port
EXPOSE 8080

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
