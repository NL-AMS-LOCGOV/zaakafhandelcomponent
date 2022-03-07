/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATABASE_SCHEMA;
import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATA_SOURCE_JNDI_NAME;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.MILESTONE;
import static org.flowable.cmmn.api.runtime.PlanItemDefinitionType.STAGE;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.ACTIVE;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.AVAILABLE;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.COMPLETED;
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
        cmmnEngineConfiguration.addPlanItemInstanceLifeCycleListener(STAGE, new UpdateZaakLifecycleListener(AVAILABLE, ACTIVE));
        cmmnEngineConfiguration.addPlanItemInstanceLifeCycleListener(MILESTONE, new UpdateZaakLifecycleListener(ACTIVE, COMPLETED));
        CaseInstanceState.END_STATES.forEach(
                endState -> cmmnEngineConfiguration.addCaseInstanceLifeCycleListener(new EndCaseLifecycleListener(CaseInstanceState.ACTIVE, endState)));
        cmmnEngineConfiguration.setCreateHumanTaskInterceptor(new CreateHumanTaskInterceptor());
        return cmmnEngineConfiguration.buildCmmnEngine();
    }

    @Override
    public void ungetCmmnEngine() {
        // Geen actie nodig.
    }
}
