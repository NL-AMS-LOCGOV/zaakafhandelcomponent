#
# SPDX-FileCopyrightText: 2021 Atos
# SPDX-License-Identifier: EUPL-1.2+
#

# Version shoud match the version used in pom.xml
export WILDFLY_VERSION=26.1.2.Final
export WILDFLY_DATASOURCES_GALLEON_PACK_VERSION=2.2.5.Final

export WILDFLY_SERVER_DIR=wildfly-$WILDFLY_VERSION

echo ">>> Installing WildFly ..."
rm -fr $WILDFLY_SERVER_DIR
galleon.sh install wildfly#$WILDFLY_VERSION --dir=$WILDFLY_SERVER_DIR --layers=jaxrs-server,microprofile-health,microprofile-fault-tolerance,elytron-oidc-client
galleon.sh install org.wildfly:wildfly-datasources-galleon-pack:$WILDFLY_DATASOURCES_GALLEON_PACK_VERSION --dir=$WILDFLY_SERVER_DIR --layers=postgresql-driver
$WILDFLY_SERVER_DIR/bin/jboss-cli.sh --file=install-wildfly.cli

# The Web Console can be enabled by:
# - adding the web-console layer to the --layers attribute
# - creating an admin userid/password combination by running: $WILDFLY_SERVER_DIR/bin/add-user.sh --user admin --group admin --password admin
