FROM openjdk:11-jdk

ARG JAR_FILE=build/libs/reborn-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} springbootapp.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/springbootapp.jar"]