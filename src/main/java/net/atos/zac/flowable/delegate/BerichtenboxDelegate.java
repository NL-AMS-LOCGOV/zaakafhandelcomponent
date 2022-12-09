/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.delegate;

import java.util.logging.Logger;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class BerichtenboxDelegate implements JavaDelegate {

    private static final Logger LOG = Logger.getLogger(BerichtenboxDelegate.class.getName());

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        LOG.info("Verstuur besluit naar Berichtenbox.");
    }
}
