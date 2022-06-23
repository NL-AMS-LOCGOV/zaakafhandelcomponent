/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.zac.event.EventingService;
import net.atos.zac.identity.IdentityService;

/**
 * A Helper for Flowable CMMN and BPMN LifecycleListener's, Interceptors etc. in order to get access to CDI resources.
 */
@ApplicationScoped
public class FlowableHelper {

    @Inject
    private FlowableService flowableService;

    @Inject
    private CaseVariablesService caseVariablesService;

    @Inject
    private TaskVariablesService taskVariablesService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private EventingService eventingService;

    @Inject
    private IdentityService identityService;

    public static FlowableHelper getInstance() {
        return CDI.current().select(FlowableHelper.class).get();
    }

    public ZGWApiService getZgwApiService() {
        return zgwApiService;
    }

    public FlowableService getFlowableService() {
        return flowableService;
    }

    public ZRCClientService getZrcClientService() {
        return zrcClientService;
    }

    public EventingService getEventingService() {
        return eventingService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public CaseVariablesService getCaseVariablesService() {
        return caseVariablesService;
    }

    public TaskVariablesService getTaskVariablesService() {
        return taskVariablesService;
    }
}
