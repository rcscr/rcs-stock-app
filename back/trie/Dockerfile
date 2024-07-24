FROM maven:3.9.7-eclipse-temurin-22

ADD . /usr/src/rcs
WORKDIR /usr/src/rcs
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]