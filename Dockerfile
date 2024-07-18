FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
ARG CONTAINER_PORT
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG CONTAINER_PORT
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${CONTAINER_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]