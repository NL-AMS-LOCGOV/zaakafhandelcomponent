/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.idm.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.IdmManagementService;
import org.flowable.idm.engine.IdmEngine;

/**
 * Makes the managed idm engine and the provided services available for injection
 */
@ApplicationScoped
public class FlowableIdmServices {

    private IdmEngine idmEngine;

    public void setIdmEngine(final IdmEngine idmEngine) {
        this.idmEngine = idmEngine;
    }

    @Produces
    @Named
    @ApplicationScoped
    public IdmEngine idmEngine() {
        return idmEngine;
    }

    @Produces
    @Named
    @ApplicationScoped
    public IdmIdentityService idmIdentityService() {
        return idmEngine.getIdmIdentityService();
    }

    @Produces
    @Named
    @ApplicationScoped
    public IdmManagementService idmManagementService() {
        return idmEngine.getIdmManagementService();
    }
}
