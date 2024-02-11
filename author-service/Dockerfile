FROM bellsoft/liberica-openjdk-alpine-musl:17
COPY target/author-service-1.0.jar app.jar
EXPOSE 9876
ENTRYPOINT ["java", "-jar", "app.jar"]