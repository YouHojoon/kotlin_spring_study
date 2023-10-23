FROM openjdk:19
ARG JAR_FILE=build/libs/main_server-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]


