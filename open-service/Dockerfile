FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src/

RUN mvn package -DskipTests

FROM openjdk:17-oracle
COPY --from=builder /app/target/open-service-1.0.0-SNAPSHOT.jar /openservice.jar
ENV PORT 9060
EXPOSE 9060

CMD ["java", "-Dserver.port=${PORT}", "-jar", "/openservice.jar"]
