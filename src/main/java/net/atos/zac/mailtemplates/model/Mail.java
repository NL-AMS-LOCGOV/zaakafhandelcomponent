/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public enum Mail {
    ZAAK_ALGEMEEN(MailTemplateVariabelen.ZAAK_VOORTGANG_VARIABELEN),
    ZAAK_ONTVANKELIJK(MailTemplateVariabelen.ZAAK_VOORTGANG_VARIABELEN),
    ZAAK_NIET_ONTVANKELIJK(MailTemplateVariabelen.ZAAK_VOORTGANG_VARIABELEN),
    ZAAK_AFGEHANDELD(MailTemplateVariabelen.ZAAK_VOORTGANG_VARIABELEN),
    TAAK_AANVULLENDE_INFORMATIE(MailTemplateVariabelen.ACTIE_VARIABELEN),
    TAAK_ONTVANGSTBEVESTIGING(MailTemplateVariabelen.ACTIE_VARIABELEN),
    TAAK_ADVIES_EXTERN(MailTemplateVariabelen.ACTIE_VARIABELEN),
    SIGNALERING_ZAAK_DOCUMENT_TOEGEVOEGD(MailTemplateVariabelen.DOCUMENT_SIGNALERING_VARIABELEN),
    SIGNALERING_ZAAK_OP_NAAM(MailTemplateVariabelen.ZAAK_SIGNALERING_VARIABELEN),
    SIGNALERING_ZAAK_VERLOPEND_STREEFDATUM(MailTemplateVariabelen.ZAAK_SIGNALERING_VARIABELEN),
    SIGNALERING_ZAAK_VERLOPEND_FATALE_DATUM(MailTemplateVariabelen.ZAAK_SIGNALERING_VARIABELEN),
    SIGNALERING_TAAK_OP_NAAM(MailTemplateVariabelen.TAAK_SIGNALERING_VARIABELEN),
    SIGNALERING_TAAK_VERLOPEN(MailTemplateVariabelen.TAAK_SIGNALERING_VARIABELEN);

    private final Set<MailTemplateVariabelen> variabelen;

    Mail(final Set<MailTemplateVariabelen> variabelen) {
        this.variabelen = Collections.unmodifiableSet(variabelen);
    }

    public Set<MailTemplateVariabelen> getVariabelen() {
        return variabelen;
    }

    public static List<Mail> getKoppelbareMails() {
        return Stream.of(ZAAK_ALGEMEEN, ZAAK_ONTVANKELIJK, ZAAK_NIET_ONTVANKELIJK, ZAAK_AFGEHANDELD,
                         TAAK_AANVULLENDE_INFORMATIE, TAAK_ADVIES_EXTERN, TAAK_ONTVANGSTBEVESTIGING).toList();
    }
}
