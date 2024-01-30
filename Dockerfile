FROM maven:3.8.3-openjdk-17-slim AS builder

WORKDIR /app

COPY . /app/

RUN mvn clean package

FROM amazoncorretto:17.0.9-alpine3.18

WORKDIR /app

COPY --from=builder /app/target/pnapi-backend-0.0.1-SNAPSHOT.jar /app/

EXPOSE 5396

CMD ["java", "-jar", "pnapi-backend-0.0.1-SNAPSHOT.jar"]