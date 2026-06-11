# Step 1: Download explicit container preloaded with Maven and Java 21 to compile code
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Step 2: Copy dependency configuration and source files into container
COPY pom.xml .
COPY src ./src

# Step 3: Run the build command inside the container to generate executable JAR file
RUN mvn clean package -DskipTests

# Step 4: Create a lightweight, secure container containing just the Java 21 Runtime Environment (JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Step 5: Extract the compiled .jar file using your exact pom.xml artifactId layout
COPY --from=build /app/target/vebbleAi-0.0.1-SNAPSHOT.jar app.jar

# Step 6: Expose port 8080 so web requests can reach the backend container safely
EXPOSE 8080

# Step 7: The execution trigger that tells the container to boot up your Spring Boot server
ENTRYPOINT ["java", "-jar", "app.jar"]
