FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src/

RUN mvn package -DskipTests

FROM openjdk:17-oracle
COPY --from=builder /app/target/mail-service-1.0.0-SNAPSHOT.jar /mailservice.jar
ENV PORT 9100
EXPOSE 9100

CMD ["java", "-Dserver.port=${PORT}", "-jar", "/mailservice.jar"]
