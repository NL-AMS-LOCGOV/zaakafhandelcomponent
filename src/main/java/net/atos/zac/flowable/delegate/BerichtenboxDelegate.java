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

    private static final String BERICHTENBOX_VERZONDEN_VARIABLE = "berichtenbox_verzonden";

    private static final String VERZENDEN_METHODE_VARIABLE = "verzend_methode";

    private static final String VERZENDEN_METHODE_POST = "post";

    private boolean verzendenGelukt = true;

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        LOG.info("Verstuur besluit via Berichtenbox.");
        LOG.info("VerzendenGelukt = %s.".formatted(verzendenGelukt));
        delegateExecution.setVariable(BERICHTENBOX_VERZONDEN_VARIABLE, verzendenGelukt);
        if (!verzendenGelukt) {
            delegateExecution.setVariable(VERZENDEN_METHODE_VARIABLE, VERZENDEN_METHODE_POST);
        }
        verzendenGelukt = !verzendenGelukt;
    }
}
