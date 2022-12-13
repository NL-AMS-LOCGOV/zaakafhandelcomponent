/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.delegate;

import java.util.logging.Logger;

import org.flowable.engine.delegate.DelegateExecution;

public class PostDelegate extends AbstractDelegate {

    private static final Logger LOG = Logger.getLogger(PostDelegate.class.getName());

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        LOG.info("Verstuur besluit van zaak '%s' via %s.".formatted(getZaakIdentificatie(delegateExecution),
                                                                    PostDelegate.class.getSimpleName()));
    }
}
