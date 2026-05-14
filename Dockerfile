FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn clean compile -DskipTests -pl languagetool-server
EXPOSE 8080
CMD ["mvn", "-pl", "languagetool-server", "exec:java", "-Dexec.mainClass=org.languagetool.server.HTTPServer", "-Dexec.args=--port 8080 --public"]
