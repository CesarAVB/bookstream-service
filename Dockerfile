# -------------------------------------------------------
# Stage 1 - Build
# -------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 🔥 Corrige permissão do Maven Wrapper
RUN chmod +x mvnw

# Baixa dependências primeiro (melhor cache de build)
RUN ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# -------------------------------------------------------
# Stage 2 - Runtime
# -------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S bookstream && adduser -S bookstream -G bookstream
USER bookstream

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]