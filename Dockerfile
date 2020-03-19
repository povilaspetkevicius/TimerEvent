FROM adoptopenjdk/openjdk8:alpine-jre
WORKDIR /usr/local/share
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]