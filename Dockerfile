### The following versions should be equal to the ones defined in install-wildfly.sh
ARG MAVEN_VERSION=3.8.4-openjdk-17

### Maven build fase
FROM maven:$MAVEN_VERSION as build
COPY . /
RUN mvn -DskipTests package

### Create runtime image fase
FROM eclipse-temurin:17-jre-focal as runtime

# Copy zaakafhandelcomponent bootable jar
COPY --from=build /target/zaakafhandelcomponent.jar /

# Start zaakafhandelcomponent
ENTRYPOINT ["java", "-jar", "zaakafhandelcomponent.jar"]
EXPOSE 8080 8787 9990
