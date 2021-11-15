#
# SPDX-FileCopyrightText: 2021 Atos
# SPDX-License-Identifier: EUPL-1.2+
#

export WILDFLY_VERSION=24.0.1.Final
export WILDFLY_SERVER_DIR=wildfly-$WILDFLY_VERSION

echo ">>> Installing WildFly ..."
rm -fr $WILDFLY_SERVER_DIR
galleon.sh install wildfly#$WILDFLY_VERSION --dir=$WILDFLY_SERVER_DIR --layers=jaxrs-server
galleon.sh install org.wildfly:wildfly-datasources-galleon-pack:2.0.5.Final --dir=$WILDFLY_SERVER_DIR --layers=postgresql-driver
galleon.sh install org.keycloak:keycloak-adapter-galleon-pack:15.0.2 --dir=$WILDFLY_SERVER_DIR --layers=keycloak-client-oidc
cp -r extra-content-wildfly/standalone $WILDFLY_SERVER_DIR
$WILDFLY_SERVER_DIR/bin/jboss-cli.sh --file=install-wildfly.cli

