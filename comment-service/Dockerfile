FROM bellsoft/liberica-openjdk-alpine-musl:17
COPY target/comment-service-1.0.jar app.jar
EXPOSE 9877
ENTRYPOINT ["java", "-jar", "app.jar"]