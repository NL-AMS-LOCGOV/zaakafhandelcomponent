#
# SPDX-FileCopyrightText: 2021 Atos
# SPDX-License-Identifier: EUPL-1.2+
#

# The following versions should be equal to the ones defined in Dockerfile
export WILDFLY_VERSION=24.0.0.Final
export KEYCLOAK_VERSION=15.0.2
export POSTGRESQL_DRIVER_VERSION=42.2.24

# Install WildFly
export JBOSS_HOME=wildfly-$WILDFLY_VERSION
rm -fr wildfly-*
wget -O /tmp/wildfly.zip https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.zip
unzip -q /tmp/wildfly.zip -d .
rm /tmp/wildfly.zip

# Create WildFly administrative user
$JBOSS_HOME/bin/add-user.sh --user admin --group admin --password admin

# Configure Keycloak client adapter
wget -O /tmp/keycloak-wildfly-adapter-dist.zip https://github.com/keycloak/keycloak/releases/download/$KEYCLOAK_VERSION/keycloak-oidc-wildfly-adapter-$KEYCLOAK_VERSION.zip
unzip -q /tmp/keycloak-wildfly-adapter-dist.zip -d $JBOSS_HOME
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/bin/adapter-elytron-install-offline.cli
rm /tmp/keycloak-wildfly-adapter-dist.zip

# Copy the PostgreSQL driver temporarily to the current folder
wget -O postgresql-driver.jar https://jdbc.postgresql.org/download/postgresql-$POSTGRESQL_DRIVER_VERSION.jar

# Configure WildFly
${JBOSS_HOME}/bin/jboss-cli.sh --file=wildfly_configuration.cli

# Remove the PostgreSQL driver
rm postgresql-driver.jar
