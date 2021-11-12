#
# SPDX-FileCopyrightText: 2021 Atos
# SPDX-License-Identifier: EUPL-1.2+
#

# The following versions should be equal to the ones defined in Dockerfile
export WILDFLY_VERSION=24.0.1.Final
export KEYCLOAK_VERSION=15.0.2
export POSTGRESQL_DRIVER_VERSION=42.3.1

# Install WildFly
echo ">>> Downloading and unpacking WildFly ..."
export JBOSS_HOME=wildfly-$WILDFLY_VERSION
rm -fr wildfly-*
wget --no-verbose -O /tmp/wildfly.zip https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.zip
unzip -q /tmp/wildfly.zip -d .
rm /tmp/wildfly.zip


# Configure Keycloak client adapter
echo ">>> Downloading and installing Keycloak adapter ..."
wget --no-verbose -O /tmp/keycloak-wildfly-adapter-dist.zip https://github.com/keycloak/keycloak/releases/download/$KEYCLOAK_VERSION/keycloak-oidc-wildfly-adapter-$KEYCLOAK_VERSION.zip
unzip -q /tmp/keycloak-wildfly-adapter-dist.zip -d $JBOSS_HOME
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/bin/adapter-elytron-install-offline.cli
rm /tmp/keycloak-wildfly-adapter-dist.zip

# Copy the PostgreSQL driver temporarily to the current folder
echo ">>> Downloading PostgreSQL driver ..."
wget --no-verbose -O postgresql-driver.jar https://jdbc.postgresql.org/download/postgresql-$POSTGRESQL_DRIVER_VERSION.jar

echo ">>> Configuring WildFly ..."
# Create WildFly administrative user
$JBOSS_HOME/bin/add-user.sh --user admin --group admin --password admin
# Configure WildFly
${JBOSS_HOME}/bin/jboss-cli.sh --file=wildfly_configuration.cli

# Remove the PostgreSQL driver
rm postgresql-driver.jar
