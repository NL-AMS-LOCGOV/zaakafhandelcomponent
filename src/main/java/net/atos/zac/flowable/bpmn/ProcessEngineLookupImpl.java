/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.bpmn;

import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE;
import static org.flowable.common.engine.impl.history.HistoryLevel.AUDIT;
import static org.flowable.engine.impl.cfg.DelegateExpressionFieldInjectionMode.DISABLED;

import org.flowable.cdi.spi.ProcessEngineLookup;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

/**
 *
 */
public class ProcessEngineLookupImpl implements ProcessEngineLookup {

    public static final String DATABASE_SCHEMA = "flowable";

    public static final String DATA_SOURCE_JNDI_NAME = "java:comp/env/jdbc/FlowableDS";

    private static ProcessEngineConfigurationImpl processEngineConfiguration = null;

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public ProcessEngine getProcessEngine() {
        return getProcessEngineConfiguration().buildProcessEngine();
    }

    @Override
    public void ungetProcessEngine() {
        // Geen actie nodig.
    }

    public static ProcessEngineConfigurationImpl getProcessEngineConfiguration() {

        if (processEngineConfiguration == null) {
            processEngineConfiguration = new StandaloneProcessEngineConfiguration();
            processEngineConfiguration.setDataSourceJndiName(DATA_SOURCE_JNDI_NAME);
            processEngineConfiguration.setDatabaseType(DATABASE_TYPE_POSTGRES);
            processEngineConfiguration.setDatabaseSchema(DATABASE_SCHEMA);
            processEngineConfiguration.setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_TRUE);
            processEngineConfiguration.setHistoryLevel(AUDIT);
            processEngineConfiguration.setEnableSafeBpmnXml(true);
            processEngineConfiguration.setDelegateExpressionFieldInjectionMode(DISABLED);
            processEngineConfiguration.setEnableHistoricTaskLogging(true);
            processEngineConfiguration.setDisableIdmEngine(true);
            processEngineConfiguration.setAsyncExecutorActivate(false);

            processEngineConfiguration.init();
        }
        return processEngineConfiguration;
    }
}
