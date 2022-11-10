/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import java.util.Collections;
import java.util.Set;

public enum Mail {

    ZAAK_ONTVANKELIJK(MailTemplateVariabelen.ZAAKSTATUSMAIL_VARIABELEN),
    ZAAK_NIET_ONTVANKELIJK(MailTemplateVariabelen.ZAAKSTATUSMAIL_VARIABELEN),
    ZAAK_AFGEHANDELD(MailTemplateVariabelen.ZAAKSTATUSMAIL_VARIABELEN),
    PROCES_AANVULLENDE_INFORMATIE(MailTemplateVariabelen.PROCESMAIL_VARIABELEN),
    PROCES_ONTVANGSTBEVESTIGING(MailTemplateVariabelen.PROCESMAIL_VARIABELEN),
    PROCES_ADVIES(MailTemplateVariabelen.PROCESMAIL_VARIABELEN),
    SIGNALERING_ZAAK_DOCUMENT_TOEGEVOEGD(MailTemplateVariabelen.DOCUMENTSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_ZAAK_OP_NAAM(MailTemplateVariabelen.ZAAKSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_ZAAK_VERLOPEND_STREEFDATUM(MailTemplateVariabelen.ZAAKSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_ZAAK_VERLOPEND_FATALE_DATUM(MailTemplateVariabelen.ZAAKSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_TAAK_OP_NAAM(MailTemplateVariabelen.TAAKSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_TAAK_VERLOPEN(MailTemplateVariabelen.TAAKSIGNALERINGMAIL_VARIABELEN);

    private final Set<MailTemplateVariabelen> variabelen;

    Mail(final Set<MailTemplateVariabelen> variabelen) {
        this.variabelen = Collections.unmodifiableSet(variabelen);
    }

    public Set<MailTemplateVariabelen> getVariabelen() {
        return variabelen;
    }
}
