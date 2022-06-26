FROM openjdk:11
ADD ./target/megamarket-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "backend.jar"]


