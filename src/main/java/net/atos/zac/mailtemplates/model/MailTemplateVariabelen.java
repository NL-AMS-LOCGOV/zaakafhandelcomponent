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

    ZAAKNUMMER(false),
    ZAAKTYPE(false),
    ZAAKSTATUS(false),
    DOCUMENTTITEL(false),
    REGISTRATIEDATUM(false),
    STARTDATUM(false),
    STREEFDATUM(true),
    FATALEDATUM(false),
    OMSCHRIJVINGZAAK(false),
    TOELICHTINGZAAK(true),
    INITIATOR(true),
    ADRESINITIATOR(true),
    TOEGEWEZENGROEPZAAK(false),
    TOEGEWEZENGEBRUIKERZAAK(true),
    TOEGEWEZENGROEPTAAK(false),
    TOEGEWEZENGEBRUIKERTAAK(true),
    ZAAKURL(false),
    ZAAKLINK(false),
    TAAKURL(false),
    TAAKLINK(false),
    DOCUMENTURL(false),
    DOCUMENTLINK(false);

    private final boolean resolveVariabeleAlsLegeString;

    MailTemplateVariabelen(final boolean resolveVariabeleAlsLegeString) {
        this.resolveVariabeleAlsLegeString = resolveVariabeleAlsLegeString;
    }

    public boolean isResolveVariabeleAlsLegeString() {
        return resolveVariabeleAlsLegeString;
    }

    // Sets of variables for mail templates for specific subject types
    private static final Set<MailTemplateVariabelen> ZAAK_VARIABELEN =
            toSet(ZAAKNUMMER, ZAAKTYPE, ZAAKSTATUS,
                  REGISTRATIEDATUM, STARTDATUM, STREEFDATUM, FATALEDATUM,
                  OMSCHRIJVINGZAAK, TOELICHTINGZAAK);

    private static final Set<MailTemplateVariabelen> TAAK_VARIABELEN =
            toSet();

    private static final Set<MailTemplateVariabelen> DOCUMENT_VARIABELEN =
            toSet(DOCUMENTTITEL);

    // Set of variables for templates for mail that will be sent to a klant or bedrijf
    private static final Set<MailTemplateVariabelen> ZAAK_INITIATOR_VARIABELEN =
            toSet(INITIATOR, ADRESINITIATOR);

    // Sets of variables for templates for mail that will be sent to a gebruiker
    private static final Set<MailTemplateVariabelen> ZAAK_BEHANDELAAR_VARIABELEN =
            toSet(ZAAKURL, ZAAKLINK,
                  TOEGEWEZENGROEPZAAK, TOEGEWEZENGEBRUIKERZAAK);

    private static final Set<MailTemplateVariabelen> TAAK_BEHANDELAAR_VARIABELEN =
            toSet(TAAKURL, TAAKLINK,
                  TOEGEWEZENGROEPTAAK, TOEGEWEZENGEBRUIKERTAAK);

    private static final Set<MailTemplateVariabelen> DOCUMENT_BEHANDELAAR_VARIABELEN =
            toSet(DOCUMENTURL, DOCUMENTLINK);

    // Sets of variables for mail templates for specific uses cases
    public static final Set<MailTemplateVariabelen> ZAAK_VOORTGANG_VARIABELEN =
            add(ZAAK_VARIABELEN, ZAAK_INITIATOR_VARIABELEN);

    public static final Set<MailTemplateVariabelen> PROCES_VARIABELEN =
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
            final MailTemplateVariabelen... values) {
        return add(set, toEnumSet(Arrays.asList(values)));
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
