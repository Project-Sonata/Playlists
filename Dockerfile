# Build stage
FROM gradle:8.3.0-jdk17 AS build

COPY build.gradle /app/
COPY settings.gradle /app/

WORKDIR /app

RUN --mount=type=secret,id=github.username \
    --mount=type=secret,id=github.token \
    gradle resolveDependencies --no-daemon

COPY src/main /app/src/main/

RUN gradle bootJar -x test --no-daemon

# Runtime
FROM wodby/openjdk:17-jre-alpine AS runtime
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]