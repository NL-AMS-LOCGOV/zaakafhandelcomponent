/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;

import javax.annotation.ManagedBean;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.event.AbstractEventObserver;

/**
 * Deze bean luistert naar CacheUpdateEvents, en werkt daar vervolgens de caches mee bij.
 */
@ManagedBean
public class CacheEventObserver extends AbstractEventObserver<CacheEvent> {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    public void onFire(final @Observes CacheEvent event) {
        switch (event.getObjectType()) {
            case ZAAKROL:
                zgwApiService.updateZaakbehandelaarCache(event.getUrl());
                zgwApiService.updateZaakgroepCache(event.getUrl());
                break;
            case ZAAKSTATUS:
                zrcClientService.updateZaakstatusCache(event.getUrl());
                break;
            case ZAAKTYPE:
                ztcClientService.updateZaaktypeStatustypeCache(event.getUrl());
                ztcClientService.updateZaaktypeResultaattypeCache(event.getUrl());
                ztcClientService.clearZaaktypeRoltypeCache();
                ztcClientService.updateZaaktypeCache(event.getUrl());
                ztcClientService.updateZaaktypeCache(event.getUUID());
                ztcClientService.clearZaaktypeUrlCache();
                ztcClientService.clearZaaktypeCache();
                break;
        }
    }
}
