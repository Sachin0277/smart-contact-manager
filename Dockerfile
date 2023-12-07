# Use an official Maven and OpenJDK 18 image as a parent image for the build stage
FROM maven:4.0.0-openjdk-18-slim AS build


# Copy the Maven configuration files
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# Use an official OpenJDK 18 runtime image as a base image for the final stage
FROM adoptopenjdk:18-jre-hotspot-slim


# Copy the JAR file built by Maven into the container
COPY --from=build /target/smartcontactmanager-0.0.1-SNAPSHOT.jar smartcontactmanager.jar

# Expose the port that your application will run on
EXPOSE 8080

# Command to run your application
ENTRYPOINT ["java", "-jar", "smartcontactmanager.jar"]
