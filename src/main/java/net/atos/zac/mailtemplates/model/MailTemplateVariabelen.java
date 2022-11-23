/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum MailTemplateVariabelen {

    ZAAKNUMMER(false),
    ZAAKTYPE(false),
    ZAAKSTATUS(false),
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
    TAAKURL(false);

    private final boolean resolveVariabeleAlsLegeString;

    MailTemplateVariabelen(final boolean resolveVariabeleAlsLegeString) {
        this.resolveVariabeleAlsLegeString = resolveVariabeleAlsLegeString;
    }

    public boolean isResolveVariabeleAlsLegeString() {
        return resolveVariabeleAlsLegeString;
    }

    private static final Set<MailTemplateVariabelen> DEFAULT =
            EnumSet.of(ZAAKNUMMER, ZAAKTYPE, ZAAKSTATUS, REGISTRATIEDATUM, STARTDATUM, STREEFDATUM, FATALEDATUM,
                       OMSCHRIJVINGZAAK, TOELICHTINGZAAK);

    public static final Set<MailTemplateVariabelen> ZAAKSTATUSMAIL_VARIABELEN =
            addToDefault(EnumSet.of(INITIATOR, ADRESINITIATOR));

    public static final Set<MailTemplateVariabelen> PROCESMAIL_VARIABELEN =
            addToDefault(EnumSet.of(INITIATOR, ADRESINITIATOR));

    public static final Set<MailTemplateVariabelen> ZAAKSIGNALERINGMAIL_VARIABELEN =
            addToDefault(EnumSet.of(ZAAKURL, TOEGEWEZENGROEPZAAK, TOEGEWEZENGEBRUIKERZAAK));

    public static final Set<MailTemplateVariabelen> TAAKSIGNALERINGMAIL_VARIABELEN =
            addToDefault(EnumSet.of(TAAKURL, TOEGEWEZENGROEPTAAK, TOEGEWEZENGEBRUIKERTAAK));

    private static Set<MailTemplateVariabelen> addToDefault(final Set<MailTemplateVariabelen> extraVariabelen) {
        final Set<MailTemplateVariabelen> returnSet = new HashSet<>();
        returnSet.addAll(DEFAULT);
        returnSet.addAll(extraVariabelen);
        return returnSet;
    }

    public String getVariabele() {
        return String.format("{%s}", this);
    }
}
