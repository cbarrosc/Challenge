# Etapa de construcción
FROM eclipse-temurin:21-jdk-jammy as builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar archivos de construcción y código fuente
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle
COPY src ./src

# Dar permisos de ejecución al script gradlew
RUN chmod +x gradlew

# Construir la aplicación
RUN ./gradlew build --no-daemon

# Etapa final
FROM eclipse-temurin:21-jre-jammy

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR generado
COPY --from=builder /app/build/libs/challenge-app-*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]