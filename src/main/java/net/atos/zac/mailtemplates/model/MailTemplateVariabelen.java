/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum MailTemplateVariabelen {

    DOCUMENT_TITEL(false),
    DOCUMENT_LINK(false),
    DOCUMENT_URL(false),
    GEMEENTE(false),
    TAAK_BEHANDELAAR_GROEP(false),
    TAAK_BEHANDELAAR_MEDEWERKER(true),
    TAAK_FATALEDATUM(false),
    TAAK_LINK(false),
    TAAK_URL(false),
    ZAAK_BEHANDELAAR_GROEP(false),
    ZAAK_BEHANDELAAR_MEDEWERKER(true),
    ZAAK_FATALEDATUM(false),
    ZAAK_INITIATOR(true),
    ZAAK_INITIATOR_ADRES(true),
    ZAAK_LINK(false),
    ZAAK_NUMMER(false),
    ZAAK_OMSCHRIJVING(false),
    ZAAK_REGISTRATIEDATUM(false),
    ZAAK_STARTDATUM(false),
    ZAAK_STATUS(false),
    ZAAK_STREEFDATUM(true),
    ZAAK_TOELICHTING(true),
    ZAAK_TYPE(false),
    ZAAK_URL(false);

    private final boolean resolveVariabeleAlsLegeString;

    MailTemplateVariabelen(final boolean resolveVariabeleAlsLegeString) {
        this.resolveVariabeleAlsLegeString = resolveVariabeleAlsLegeString;
    }

    public boolean isResolveVariabeleAlsLegeString() {
        return resolveVariabeleAlsLegeString;
    }

    // Sets of variables for mail templates for specific subject types
    private static final Set<MailTemplateVariabelen> GEMEENTE_VARIABELEN =
            toSet(GEMEENTE);

    // Sets of variables for mail templates for specific subject types
    private static final Set<MailTemplateVariabelen> ZAAK_VARIABELEN =
            toSet(ZAAK_NUMMER, ZAAK_TYPE, ZAAK_STATUS,
                  ZAAK_REGISTRATIEDATUM, ZAAK_STARTDATUM, ZAAK_STREEFDATUM, ZAAK_FATALEDATUM,
                  ZAAK_OMSCHRIJVING, ZAAK_TOELICHTING);

    private static final Set<MailTemplateVariabelen> TAAK_VARIABELEN =
            toSet(TAAK_FATALEDATUM);

    private static final Set<MailTemplateVariabelen> DOCUMENT_VARIABELEN =
            toSet(DOCUMENT_TITEL);

    // Set of variables for templates for mail that will be sent to a klant or bedrijf
    private static final Set<MailTemplateVariabelen> ZAAK_INITIATOR_VARIABELEN =
            toSet(ZAAK_INITIATOR, ZAAK_INITIATOR_ADRES);

    // Sets of variables for templates for mail that will be sent to a gebruiker
    private static final Set<MailTemplateVariabelen> ZAAK_BEHANDELAAR_VARIABELEN =
            toSet(ZAAK_URL, ZAAK_LINK,
                  ZAAK_BEHANDELAAR_GROEP, ZAAK_BEHANDELAAR_MEDEWERKER);

    private static final Set<MailTemplateVariabelen> TAAK_BEHANDELAAR_VARIABELEN =
            toSet(TAAK_URL, TAAK_LINK,
                  TAAK_BEHANDELAAR_GROEP, TAAK_BEHANDELAAR_MEDEWERKER);

    private static final Set<MailTemplateVariabelen> DOCUMENT_BEHANDELAAR_VARIABELEN =
            toSet(DOCUMENT_URL, DOCUMENT_LINK);

    // Sets of variables for mail templates for specific uses cases
    public static final Set<MailTemplateVariabelen> ZAAK_VOORTGANG_VARIABELEN =
            add(add(GEMEENTE_VARIABELEN, ZAAK_VARIABELEN), ZAAK_INITIATOR_VARIABELEN);

    public static final Set<MailTemplateVariabelen> ACTIE_VARIABELEN =
            add(ZAAK_VARIABELEN, ZAAK_INITIATOR_VARIABELEN);

    public static final Set<MailTemplateVariabelen> ZAAK_SIGNALERING_VARIABELEN =
            add(ZAAK_VARIABELEN, ZAAK_BEHANDELAAR_VARIABELEN);

    public static final Set<MailTemplateVariabelen> TAAK_SIGNALERING_VARIABELEN =
            add(add(ZAAK_SIGNALERING_VARIABELEN, TAAK_VARIABELEN), TAAK_BEHANDELAAR_VARIABELEN);

    public static final Set<MailTemplateVariabelen> DOCUMENT_SIGNALERING_VARIABELEN =
            add(add(ZAAK_SIGNALERING_VARIABELEN, DOCUMENT_VARIABELEN), DOCUMENT_BEHANDELAAR_VARIABELEN);

    private static Set<MailTemplateVariabelen> toSet(
            final MailTemplateVariabelen... values) {
        return Collections.unmodifiableSet(toEnumSet(Arrays.asList(values)));
    }

    private static Set<MailTemplateVariabelen> add(
            final Set<MailTemplateVariabelen> set,
            final Set<MailTemplateVariabelen> values) {
        final EnumSet<MailTemplateVariabelen> copy = toEnumSet(set);
        copy.addAll(values);
        return Collections.unmodifiableSet(copy);
    }

    private static EnumSet<MailTemplateVariabelen> toEnumSet(
            final Collection<MailTemplateVariabelen> values) {
        return values.isEmpty()
                ? EnumSet.noneOf(MailTemplateVariabelen.class)
                : EnumSet.copyOf(values);
    }

    public String getVariabele() {
        return "{%s}".formatted(this);
    }
}
