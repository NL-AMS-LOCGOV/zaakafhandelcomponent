/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.event.AbstractUpdateObserver;

/**
 * Deze bean luistert naar CacheUpdateEvents, en werkt daar vervolgens de caches mee bij.
 */
@ManagedBean
public class CacheUpdateObserver extends AbstractUpdateObserver<CacheUpdateEvent> {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    public void onFire(final @ObservesAsync CacheUpdateEvent event) {
        switch (event.getObjectType()){
            case ZAAKROL:
                // TODO ESUITEDEV-25829
                //zrcClientService.clearZaakrolCache(event.getUrl());
                break;
            case ZAAKSTATUS:
                // TODO ESUITEDEV-25829
                //zrcClientService.clearZaakstatusCache(event.getUrl());
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
