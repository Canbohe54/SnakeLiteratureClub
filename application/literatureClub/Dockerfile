# build docker for project
FROM maven:3.8.3-openjdk-17
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=19198","-jar","/LiteratureClubApplication.jar"]