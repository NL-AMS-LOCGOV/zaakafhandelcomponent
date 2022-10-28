/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import java.util.Collections;
import java.util.Set;

public enum MailTemplateEnum {

    ZAAK_ONTVANKELIJK(MailTemplateVariabelen.ZAAKSTATUSMAIL_VARIABELEN),
    ZAAK_NIET_ONTVANKELIJK(MailTemplateVariabelen.ZAAKSTATUSMAIL_VARIABELEN),
    ZAAK_AFGEHANDELD(MailTemplateVariabelen.ZAAKSTATUSMAIL_VARIABELEN),
    PROCES_AANVULLENDE_INFORMATIE(MailTemplateVariabelen.PROCESMAIL_VARIABELEN),
    PROCES_ONTVANGSTBEVESTIGING(MailTemplateVariabelen.PROCESMAIL_VARIABELEN),
    SIGNALERING_ZAAK(MailTemplateVariabelen.ZAAKSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_TAAK(MailTemplateVariabelen.TAAKSIGNALERINGMAIL_VARIABELEN),
    SIGNALERING_DOCUMENT(MailTemplateVariabelen.DOCUMENTSIGNALERINGMAIL_VARIABELEN);

    private final Set<MailTemplateVariabelen> variabelen;

    MailTemplateEnum(final Set<MailTemplateVariabelen> variabelen) {
        this.variabelen = Collections.unmodifiableSet(variabelen);
    }

    public Set<MailTemplateVariabelen> getVariabelen() {
        return variabelen;
    }
}
