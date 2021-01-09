FROM maven:3-jdk-11 AS build
COPY . /employees-management
WORKDIR /employees-management
RUN mvn clean package

FROM openjdk:11-jre-slim
COPY --from=build /employees-management/target/employees-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]