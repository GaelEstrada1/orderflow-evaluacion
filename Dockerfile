# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9.11-eclipse-temurin-25 AS builder

WORKDIR /workspace

COPY pom.xml .
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY infraestructure/pom.xml infraestructure/
COPY boot/pom.xml boot/
COPY report/pom.xml report/

RUN mvn dependency:go-offline -B -q

COPY application/src application/src
COPY boot/src boot/src
COPY domain/src domain/src
COPY infraestructure/src infraestructure/src

RUN mvn package -DskipTests -Djacoco.skip=true -B -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre AS runtime

RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /app

COPY --from=builder /workspace/boot/target/boot-1.0-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]