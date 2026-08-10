#--Build stage --

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

#--runtime stage ---

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/images && chown -R spring:spring /app
USER spring
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
