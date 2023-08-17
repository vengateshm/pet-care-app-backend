FROM gradle:8.2.1-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean build

# Use the official OpenJDK base image
FROM azul/zulu-openjdk-alpine:17.0.8
# Set the working directory inside the container
WORKDIR /app
# Copy the Ktor application JAR file into the container
COPY --from=build /app/build/libs/dev.vengateshm.pet-care-app-0.0.1-all.jar /app/pet-care-app.jar
# Expose the port that your Ktor application listens on
EXPOSE 8888
# Command to run the Ktor application
CMD ["java", "-jar", "pet-care-app.jar"]