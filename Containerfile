### The following versions should be equal to the ones defined in install-wildfly.sh
ARG MAVEN_VERSION=3.8.4-openjdk-17

### Maven build fase
FROM docker.io/maven:$MAVEN_VERSION as build
COPY . /
RUN mvn -DskipTests package

### Create runtime image fase
FROM docker.io/eclipse-temurin:17-jre-focal as runtime

# Import certificates into Java truststore
ADD image/certificates /certificates
RUN keytool -importcert -cacerts -file /certificates/* -storepass changeit -noprompt

# Copy zaakafhandelcomponent bootable jar
COPY --from=build /target/zaakafhandelcomponent.jar /

# Start zaakafhandelcomponent
ENTRYPOINT ["java", "-jar", "zaakafhandelcomponent.jar"]
# Start zaakafhandelcomponent in debug mode
# ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8787", "-jar", "zaakafhandelcomponent.jar"]
EXPOSE 8080 8787 9990
