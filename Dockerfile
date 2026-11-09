# Stage 1: Build stage with official Maven + JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Production JRE runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
