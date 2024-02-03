FROM gradle:jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]