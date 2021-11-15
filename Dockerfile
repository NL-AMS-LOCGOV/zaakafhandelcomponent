### The following versions should be equal to the ones defined in install-wildfly.sh
ARG MAVEN_VERSION=3.8.2-jdk-11

### Maven build fase
FROM maven:$MAVEN_VERSION as build
COPY . /
RUN mvn -DskipTests package

### Create runtime image fase
FROM adoptopenjdk/openjdk11 as runtime

# Copy zaakafhandelcomponent bootable jar
COPY --from=build /target/zaakafhandelcomponent-bootable.jar /

# Start zaakafhandelcomponent
CMD ["java", "-jar", "zaakafhandelcomponent-bootable.jar"]
EXPOSE 8080 8443
