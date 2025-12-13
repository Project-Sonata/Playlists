# Build stage
FROM gradle:8.3.0-jdk17 AS build

COPY build.gradle /app/
COPY settings.gradle /app/
COPY src/main /app/src/main/

WORKDIR /app

RUN --mount=type=secret,id=github.username \
     --mount=type=secret,id=github.token \
    gradle dependencies --no-daemon

RUN --mount=type=secret,id=github.username \
    --mount=type=secret,id=github.token \
    gradle bootJar -x test --no-daemon

# Runtime

FROM eclipse-temurin:17-jre-alpine AS runtime
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
