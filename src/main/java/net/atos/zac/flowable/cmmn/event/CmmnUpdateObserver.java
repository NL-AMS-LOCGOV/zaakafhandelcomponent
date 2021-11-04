/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn.event;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.ObservesAsync;

import net.atos.zac.event.AbstractUpdateObserver;

/**
 * Deze bean luistert naar CmmnUpdateEvents, en werkt daar vervolgens flowable mee bij.
 */
@Stateless
public class CmmnUpdateObserver extends AbstractUpdateObserver<CmmnUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(CmmnUpdateObserver.class.getName());

    @Override
    public void onFire(final @ObservesAsync CmmnUpdateEvent event) {
        LOG.info("TODO CmmnUpdateEvent verwerken " + event);
    }
}
