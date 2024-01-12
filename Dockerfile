FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY src /src
COPY pom.xml .
RUN mvn clean install
RUN mvn -f pom.xml clean package
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/debeziumspring-0.0.1-SNAPSHOT.jar"]

