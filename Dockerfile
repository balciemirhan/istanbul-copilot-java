# 1. Aşama: Derleme
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Bağımlılıkları önbelleğe almak için önceden indir
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Aşama: Çalışma Zamanı
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/copilot-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
