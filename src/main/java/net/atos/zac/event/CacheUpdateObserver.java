/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.ObservesAsync;

/**
 * Deze bean luistert naar CacheUpdateEvents, en werkt daar vervolgens de caches mee bij.
 */
@Stateless
public class CacheUpdateObserver extends AbstractUpdateObserver<CacheUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(CacheUpdateObserver.class.getName());

    public void onFire(final @ObservesAsync CacheUpdateEvent event) {
        LOG.info("TODO CacheUpdateEvent verwerken " + event);
    }
}
