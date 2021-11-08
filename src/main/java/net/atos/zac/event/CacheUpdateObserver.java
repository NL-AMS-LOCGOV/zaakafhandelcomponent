/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;

/**
 * Deze bean luistert naar CacheUpdateEvents, en werkt daar vervolgens de caches mee bij.
 */
// TODO Verplaatsen naar waar dit gebruikt gaat worden .../event
@ManagedBean
public class CacheUpdateObserver extends AbstractUpdateObserver<CacheUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(CacheUpdateObserver.class.getName());

    public void onFire(final @ObservesAsync CacheUpdateEvent event) {
        LOG.info("TODO CacheUpdateEvent verwerken " + event);
    }
}
