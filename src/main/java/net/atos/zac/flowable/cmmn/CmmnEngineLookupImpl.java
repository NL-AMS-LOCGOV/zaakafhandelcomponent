/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATABASE_SCHEMA;
import static net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl.DATA_SOURCE_JNDI_NAME;
import static org.flowable.cmmn.engine.impl.cfg.DelegateExpressionFieldInjectionMode.DISABLED;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE;
import static org.flowable.common.engine.impl.history.HistoryLevel.AUDIT;

import org.flowable.cdi.spi.CmmnEngineLookup;
import org.flowable.cmmn.api.runtime.CaseInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.configurator.impl.process.DefaultProcessInstanceService;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.impl.agenda.AgendaSessionFactory;
import org.flowable.engine.impl.agenda.DefaultFlowableEngineAgendaFactory;

import net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl;

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
        cmmnEngineConfiguration.setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_TRUE);
        cmmnEngineConfiguration.setHistoryLevel(AUDIT);
        cmmnEngineConfiguration.setEnableSafeCmmnXml(true);
        cmmnEngineConfiguration.setDelegateExpressionFieldInjectionMode(DISABLED);
        cmmnEngineConfiguration.setEnableHistoricTaskLogging(true);
        CaseInstanceState.END_STATES.forEach(
                endState -> cmmnEngineConfiguration.addCaseInstanceLifeCycleListener(
                        new EndCaseLifecycleListener(CaseInstanceState.ACTIVE, endState)));
        cmmnEngineConfiguration.setCreateHumanTaskInterceptor(new CreateHumanTaskInterceptor());
        cmmnEngineConfiguration.setIdentityLinkInterceptor(new CompleteTaskInterceptor(cmmnEngineConfiguration));
        cmmnEngineConfiguration.setDisableIdmEngine(true);
        cmmnEngineConfiguration.setProcessInstanceService(
                new DefaultProcessInstanceService(ProcessEngineLookupImpl.getProcessEngineConfiguration()));
        cmmnEngineConfiguration.addEngineConfiguration(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG,
                                                       ScopeTypes.CMMN,
                                                       ProcessEngineLookupImpl.getProcessEngineConfiguration());

        final var cmmnEngine = cmmnEngineConfiguration.buildCmmnEngine();

        cmmnEngineConfiguration.getSessionFactories().put(
                FlowableEngineAgenda.class,
                new AgendaSessionFactory(new DefaultFlowableEngineAgendaFactory()));

        return cmmnEngine;
    }

    @Override
    public void ungetCmmnEngine() {
        // Geen actie nodig.
    }
}
