/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.bpmn;

import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES;
import static org.flowable.common.engine.impl.AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE;
import static org.flowable.common.engine.impl.history.HistoryLevel.AUDIT;
import static org.flowable.engine.impl.cfg.DelegateExpressionFieldInjectionMode.DISABLED;

import java.util.List;

import org.flowable.cdi.spi.ProcessEngineLookup;
import org.flowable.cmmn.api.runtime.CaseInstanceState;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.configurator.CmmnEngineConfigurator;
import org.flowable.cmmn.engine.impl.cfg.DelegateExpressionFieldInjectionMode;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import net.atos.zac.flowable.cmmn.CompleteTaskInterceptor;
import net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor;
import net.atos.zac.flowable.cmmn.EndCaseLifecycleListener;

/**
 *
 */
public class ProcessEngineLookupImpl implements ProcessEngineLookup {

    public static final String DATABASE_SCHEMA = "flowable";

    public static final String DATA_SOURCE_JNDI_NAME = "java:comp/env/jdbc/FlowableDS";

    private static ProcessEngine sharedProcessEngine = null;

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public ProcessEngine getProcessEngine() {
        return getSharedProcessEngine();
    }

    @Override
    public void ungetProcessEngine() {
        // Geen actie nodig.
    }

    public static CmmnEngineConfiguration getCmmnEngineConfiguration() {
        return (CmmnEngineConfiguration) getSharedProcessEngine().getProcessEngineConfiguration()
                .getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
    }

    private static ProcessEngine getSharedProcessEngine() {
        if (sharedProcessEngine == null) {
            sharedProcessEngine = getProcessEngineConfiguration().buildProcessEngine();
        }
        return sharedProcessEngine;
    }

    private static ProcessEngineConfiguration getProcessEngineConfiguration() {
        var processEngineConfiguration = new StandaloneProcessEngineConfiguration();
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
        processEngineConfiguration.setCreateUserTaskInterceptor(new CreateUserTaskInterceptor());
        final var cmmnEngineConfigurator = new CmmnEngineConfigurator();
        cmmnEngineConfigurator.setCmmnEngineConfiguration(getCmmnEngineConfigurationHelper());
        processEngineConfiguration.setConfigurators(List.of(cmmnEngineConfigurator));
        return processEngineConfiguration;
    }

    private static CmmnEngineConfiguration getCmmnEngineConfigurationHelper() {
        final var cmmnEngineConfiguration = new CmmnEngineConfiguration();
        cmmnEngineConfiguration.setDataSourceJndiName(DATA_SOURCE_JNDI_NAME);
        cmmnEngineConfiguration.setDatabaseType(DATABASE_TYPE_POSTGRES);
        cmmnEngineConfiguration.setDatabaseSchema(DATABASE_SCHEMA);
        cmmnEngineConfiguration.setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_TRUE);
        cmmnEngineConfiguration.setHistoryLevel(AUDIT);
        cmmnEngineConfiguration.setEnableSafeCmmnXml(true);
        cmmnEngineConfiguration.setDelegateExpressionFieldInjectionMode(DelegateExpressionFieldInjectionMode.DISABLED);
        cmmnEngineConfiguration.setEnableHistoricTaskLogging(true);
        CaseInstanceState.END_STATES.forEach(
                endState -> cmmnEngineConfiguration.addCaseInstanceLifeCycleListener(
                        new EndCaseLifecycleListener(CaseInstanceState.ACTIVE, endState)));
        cmmnEngineConfiguration.setCreateHumanTaskInterceptor(new CreateHumanTaskInterceptor());
        cmmnEngineConfiguration.setIdentityLinkInterceptor(new CompleteTaskInterceptor(cmmnEngineConfiguration));
        cmmnEngineConfiguration.setDisableIdmEngine(true);
        return cmmnEngineConfiguration;
    }
}
