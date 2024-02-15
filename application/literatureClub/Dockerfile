# Use OpenJDK 17 as the base image
FROM openjdk:17

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 19198

# Add the application's jar to the container
ADD target/literatureClub-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
