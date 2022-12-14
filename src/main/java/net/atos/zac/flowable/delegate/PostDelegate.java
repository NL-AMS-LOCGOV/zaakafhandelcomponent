/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.delegate;

import java.util.logging.Logger;

import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;

public class PostDelegate extends AbstractDelegate {

    private static final Logger LOG = Logger.getLogger(PostDelegate.class.getName());

    private FixedValue template;

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        LOG.info("Verstuur per post besluit van zaak '%s' via %s met template '%s'."
                         .formatted(getZaakIdentificatie(delegateExecution), PostDelegate.class.getSimpleName(),
                                    template.getExpressionText()));
    }
}
