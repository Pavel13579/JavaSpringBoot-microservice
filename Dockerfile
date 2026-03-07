# 1. szakasz: Fordítás
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Itt történik a varázslat: a konténeren belül fut le a fordítás
RUN mvn clean package -DskipTests

# 2. szakasz: Futtatás (Eclipse Temurin-t használunk az Alpine helyett)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]