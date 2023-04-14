### Maven build fase
FROM docker.io/maven:3.8-openjdk-17 as build
COPY . /
RUN date --iso-8601='seconds' > /build_timestamp.txt && mvn package -DskipTests -Drevision=${versienummer:-latest-SNAPSHOT}

### Create runtime image fase
FROM docker.io/eclipse-temurin:17-jre-focal as runtime

# Import certificates into Java truststore and register build timestamp
ADD image/certificates /certificates
RUN keytool -importcert -cacerts -file /certificates/* -storepass changeit -noprompt

# Copy zaakafhandelcomponent bootable jar
COPY --from=build /target/zaakafhandelcomponent.jar /

# Copy build timestamp (see also HealthCheckService.java)
COPY --from=build /build_timestamp.txt /

# Start zaakafhandelcomponent
ENTRYPOINT ["java", "-jar", "zaakafhandelcomponent.jar", "--enable-preview"]
EXPOSE 8080 9990

ARG buildId
ARG commit
ARG versienummer
ENV BUILD_ID=$buildId COMMIT=$commit VERSIENUMMER=$versienummer
