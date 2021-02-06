FROM openjdk:11

COPY build/libs/martini-0.1-all.jar /application.jar

ENTRYPOINT java -jar /application.jar
