FROM bellsoft/liberica-openjdk-alpine-musl:17
COPY target/book-service-1.0.jar app.jar
EXPOSE 8765
ENTRYPOINT ["java", "-jar", "app.jar"]