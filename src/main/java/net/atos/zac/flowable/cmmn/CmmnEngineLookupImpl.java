/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATABASE_SCHEMA;
import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATA_SOURCE_JNDI_NAME;
import static org.flowable.cmmn.engine.impl.cfg.DelegateExpressionFieldInjectionMode.DISABLED;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE;
import static org.flowable.common.engine.impl.history.HistoryLevel.AUDIT;

import org.flowable.cdi.spi.CmmnEngineLookup;
import org.flowable.cmmn.api.runtime.CaseInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;

public class CmmnEngineLookupImpl implements CmmnEngineLookup {

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public CmmnEngine getCmmnEngine() {
        final CmmnEngineConfiguration cmmnEngineConfiguration = new CmmnEngineConfiguration();

        cmmnEngineConfiguration.setDataSourceJndiName(DATA_SOURCE_JNDI_NAME);
        cmmnEngineConfiguration.setDatabaseType(DATABASE_TYPE_POSTGRES);
        cmmnEngineConfiguration.setDatabaseSchema(DATABASE_SCHEMA);
        cmmnEngineConfiguration.setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_FALSE);
        cmmnEngineConfiguration.setHistoryLevel(AUDIT);
        cmmnEngineConfiguration.setEnableSafeCmmnXml(true);
        cmmnEngineConfiguration.setDelegateExpressionFieldInjectionMode(DISABLED);
        cmmnEngineConfiguration.setEnableHistoricTaskLogging(true);
        CaseInstanceState.END_STATES.forEach(
                endState -> cmmnEngineConfiguration.addCaseInstanceLifeCycleListener(new EndCaseLifecycleListener(CaseInstanceState.ACTIVE, endState)));
        cmmnEngineConfiguration.setCreateHumanTaskInterceptor(new CreateHumanTaskInterceptor());
        cmmnEngineConfiguration.setIdentityLinkInterceptor(new CompleteTaskInterceptor(cmmnEngineConfiguration));
        cmmnEngineConfiguration.setDisableIdmEngine(true);

        return cmmnEngineConfiguration.buildCmmnEngine();
    }

    @Override
    public void ungetCmmnEngine() {
        // Geen actie nodig.
    }
}
