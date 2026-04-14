# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy parent pom
COPY pom.xml .

# Copy all modules (multi-module project)
COPY api-gateway api-gateway
COPY booking-service booking-service
COPY checkin-service checkin-service
COPY com.anuj.checkin com.anuj.checkin
COPY discovery-server discovery-server
COPY flight-service flight-service
COPY payment-service payment-service
COPY swagger-service swagger-service
COPY user-service user-service

# Build ONLY flight-service with dependencies
RUN mvn clean package -pl flight-service -am -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/flight-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar","--spring.profiles.active=docker"]

