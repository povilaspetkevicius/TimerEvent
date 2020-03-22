FROM adoptopenjdk/openjdk8:alpine-jre
WORKDIR /usr/local/share
COPY target/*.jar /app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]