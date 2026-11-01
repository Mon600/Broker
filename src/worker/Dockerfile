# syntax=docker/dockerfile:1

# ---------- Stage 1: сборка bootJar (Gradle wrapper из репозитория, нужен Git LFS) ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN sed -i 's/\r$//' gradlew
RUN --mount=type=cache,target=/root/.gradle \
    sh ./gradlew --no-daemon bootJar -x test \
    && cp "$(find build/libs -maxdepth 1 -name '*.jar' ! -name '*-plain.jar' | head -n 1)" /workspace/app.jar

# ---------- Stage 2: лёгкий runtime (только JRE 21) ----------
# Worker не слушает HTTP-порт, поэтому EXPOSE нет
FROM eclipse-temurin:21-jre
RUN groupadd --system app && useradd --system --gid app app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar ./app.jar
USER app

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
