FROM bellsoft/liberica-openjdk-alpine-musl:17
COPY target/vote-service-1.0.jar app.jar
EXPOSE 9885
ENTRYPOINT ["java", "-jar", "app.jar"]