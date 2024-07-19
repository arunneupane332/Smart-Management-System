FROM ubuntu:latest AS build
RUN apt-get update && \apt-get install -y openjdk-21-jdk maven
COPY . /workspaces/Smart-Management-System/SpringBootTutorialProject/src
WORKDIR . /workspaces/Smart-Management-System/SpringBootTutorialProject/src