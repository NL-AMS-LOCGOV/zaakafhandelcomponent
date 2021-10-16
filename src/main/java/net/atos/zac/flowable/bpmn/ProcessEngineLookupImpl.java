/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.bpmn;

import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE;
import static org.flowable.common.engine.impl.history.HistoryLevel.AUDIT;
import static org.flowable.engine.impl.cfg.DelegateExpressionFieldInjectionMode.DISABLED;

import org.flowable.cdi.CdiStandaloneProcessEngineConfiguration;
import org.flowable.cdi.spi.ProcessEngineLookup;
import org.flowable.engine.ProcessEngine;

/**
 *
 */
public class ProcessEngineLookupImpl implements ProcessEngineLookup {

    public static final String DATABASE_SCHEMA = "flowable";

    public static final String DATA_SOURCE_JNDI_NAME = "java:comp/env/jdbc/FlowableDS";

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public ProcessEngine getProcessEngine() {
        final CdiStandaloneProcessEngineConfiguration processEngineConfiguration = new CdiStandaloneProcessEngineConfiguration();

        processEngineConfiguration.setDataSourceJndiName(DATA_SOURCE_JNDI_NAME);
        processEngineConfiguration.setDatabaseType(DATABASE_TYPE_POSTGRES);
        processEngineConfiguration.setDatabaseSchema(DATABASE_SCHEMA);
        processEngineConfiguration.setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_FALSE);
        processEngineConfiguration.setHistoryLevel(AUDIT);
        processEngineConfiguration.setEnableSafeBpmnXml(true);
        processEngineConfiguration.setDelegateExpressionFieldInjectionMode(DISABLED);

        return processEngineConfiguration.buildProcessEngine();
    }

    @Override
    public void ungetProcessEngine() {
        // Geen actie nodig.
    }
}
