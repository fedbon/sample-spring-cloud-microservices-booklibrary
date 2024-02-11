FROM bellsoft/liberica-openjdk-alpine-musl:17
COPY target/gateway-server-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]