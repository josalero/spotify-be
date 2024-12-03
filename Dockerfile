FROM gradle:jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-alpine

ARG PORT
ARG ADMIN_PORT
ENV PORT $PORT
ENV ADMIN_PORT $ADMIN_PORT
EXPOSE $PORT $ADMIN_PORT

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/spotify-test-*.jar /app/spotify-test.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spotify-test.jar"]