FROM maven:3.5.2-jdk-8-alpine AS build

LABEL maintainer="Igor Santos at Philips"

RUN  mkdir /work

COPY  .  /work

WORKDIR /work

RUN mvn clean install 

FROM openjdk:8u222-jdk

RUN mkdir /work

COPY --from=build  /work/target/*.jar  /work/app.jar 

EXPOSE 8080

EXPOSE 8000

ENV RABBITMQ_HOSTS=localhost:5672

ENV VIRTUAL_HOSTS=/

ENV RABBITMQ_USERNAME=guest

ENV RABBITMQ_PASSWORD=guest

ENTRYPOINT ["java","-Xdebug","-Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n","-Dphilips.rabbitmq.hosts=${RABBITMQ_HOSTS}","-Dphilips.rabbitmq.virtual-host=${VIRTUAL_HOSTS}", "-Dphilips.rabbitmq.username=${RABBITMQ_USERNAME}", "-Dphilips.rabbitmq.password=${RABBITMQ_PASSWORD}","-jar", "/work/app.jar"]


