/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.event.EventingService;

/**
 * A Helper for Flowable CMMN and BPMN LifecycleListener's, Interceptors etc. in order to get access to CDI resources.
 */
@ApplicationScoped
public class FlowableHelper {

    @Inject
    private FlowableService flowableService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private EventingService eventingService;

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

    public Medewerker createMedewerker(final String gebruikersnaam) {
        final User user = flowableService.readUser(gebruikersnaam);
        if (user == null) {
            throw new RuntimeException(String.format("Gebruiker met gebruikersnaam '%s' is niet bekend.", gebruikersnaam));
        }
        final List<Group> groups = flowableService.listGroupsForUser(gebruikersnaam);
        return new Medewerker(user, groups);
    }
}
