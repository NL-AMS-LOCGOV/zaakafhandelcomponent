### The following versions should be equal to the ones defined in install-wildfly.sh
ARG WILDFLY_VERSION=24.0.0.Final
ARG KEYCLOAK_VERSION=15.0.2
ARG POSTGRESQL_DRIVER_VERSION=42.2.24
ARG MAVEN_VERSION=3.8.2-jdk-11

### Maven build fase
FROM maven:$MAVEN_VERSION as build
COPY . /
RUN mvn -DskipTests package

### Create runtime image fase
FROM jboss/wildfly:$WILDFLY_VERSION as runtime

# Create WildFly administrative user
RUN $JBOSS_HOME/bin/add-user.sh --user admin --group admin --password admin --silent

# Change working folder to /tmp
WORKDIR /tmp

# Configure Keycloak client adapter
ARG KEYCLOAK_VERSION
ADD --chown=jboss:root https://github.com/keycloak/keycloak/releases/download/$KEYCLOAK_VERSION/keycloak-oidc-wildfly-adapter-$KEYCLOAK_VERSION.zip ./keycloak-wildfly-adapter-dist.zip
RUN unzip -qq keycloak-wildfly-adapter-dist.zip -d $JBOSS_HOME \
    && $JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/bin/adapter-elytron-install-offline.cli

# Configure WildFly
# Empty standalone_xml_history/current folder to prevent error during first startup
ARG POSTGRESQL_DRIVER_VERSION
ADD --chown=jboss:root https://jdbc.postgresql.org/download/postgresql-$POSTGRESQL_DRIVER_VERSION.jar ./postgresql-driver.jar
COPY --chown=jboss:root wildfly_configuration.cli ./
RUN  $JBOSS_HOME/bin/jboss-cli.sh --file=wildfly_configuration.cli \
     && rm -fr $JBOSS_HOME/standalone/configuration/standalone_xml_history/current

# Deploy zaakafhandelcomponent
COPY --from=build /target/zaakafhandelcomponent.war $JBOSS_HOME/standalone/deployments/
ENTRYPOINT ["/opt/jboss/wildfly/bin/standalone.sh"]
CMD ["-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug", "*:8787"]
EXPOSE 8080 9990 8787
