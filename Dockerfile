FROM gradle:8.5-jdk21 AS build
WORKDIR /home/gradle/src
COPY settings.gradle .
COPY app/ ./app
RUN gradle jar

FROM eclipse-temurin:21.0.1_12-jdk-ubi9-minimal

WORKDIR /app

COPY --from=build /home/gradle/src/app/build/libs/*.jar app.jar

CMD ["java", "-jar", "./app.jar"]