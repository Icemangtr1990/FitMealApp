# Build: compila el WAR con Maven y JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw -B -DskipTests dependency:go-offline
COPY src ./src
RUN ./mvnw -B -DskipTests clean package

# Runtime: solo JRE + el WAR
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/FitMealApp-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.war"]
