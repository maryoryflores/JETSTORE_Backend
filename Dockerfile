# Etapa de construcción: compila el JAR con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Las dependencias SNAPSHOT de Spring Boot se descargan desde repo.spring.io/snapshot
RUN mvn -B -DskipTests clean package

# Etapa de ejecución: solo el JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
