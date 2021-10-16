/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.idm;

import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATABASE_SCHEMA;
import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATA_SOURCE_JNDI_NAME;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE;

import org.flowable.cdi.spi.IdmEngineLookup;
import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.engine.IdmEngineConfiguration;

public class IdmEngineLookupImpl implements IdmEngineLookup {

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public IdmEngine getIdmEngine() {
        final IdmEngineConfiguration idmEngineConfiguration = new IdmEngineConfiguration();

        idmEngineConfiguration.setDataSourceJndiName(DATA_SOURCE_JNDI_NAME);
        idmEngineConfiguration.setDatabaseType(DATABASE_TYPE_POSTGRES);
        idmEngineConfiguration.setDatabaseSchema(DATABASE_SCHEMA);
        idmEngineConfiguration.setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_FALSE);

        return idmEngineConfiguration.buildIdmEngine();
    }

    @Override
    public void ungetIdmEngine() {
        // Geen actie nodig.
    }
}
