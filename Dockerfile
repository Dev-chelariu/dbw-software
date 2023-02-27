FROM openjdk:11
ADD target/dbwsoftware-1.0-SNAPSHOT.jar dbwsoftware.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "dbwsoftware.jar"]
