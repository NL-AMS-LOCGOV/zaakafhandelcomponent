/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mailtemplates.model;

import java.util.EnumSet;
import java.util.Set;

public enum MailTemplateVariabelen {

    ZAAKNUMMER,
    ZAAKTYPE,
    ZAAKSTATUS,
    REGISTRATIEDATUM,
    STARTDATUM,
    STREEFDATUM,
    FATALEDATUM,
    OMSCHRIJVINGZAAK,
    TOELICHTINGZAAK,
    INITIATOR,
    ADRESINITIATOR,
    TOEGEWEZENGROEPZAAK,
    TOEGEWEZENGEBRUIKERZAAK,
    TOEGEWEZENGROEPTAAK,
    TOEGEWEZENGEBRUIKERTAAK,
    ZAAKURL,
    TAAKURL,
    DOCUMENTURL,
    DOCUMENTTITEL;

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

    public static final Set<MailTemplateVariabelen> DOCUMENTSIGNALERINGMAIL_VARIABELEN =
            addToDefault(EnumSet.of(ZAAKURL, TOEGEWEZENGROEPZAAK, TOEGEWEZENGEBRUIKERZAAK, DOCUMENTTITEL, DOCUMENTURL));

    private static Set<MailTemplateVariabelen> addToDefault(final Set<MailTemplateVariabelen> extraVariabelen) {
        MailTemplateVariabelen.DEFAULT.addAll(extraVariabelen);
        return MailTemplateVariabelen.DEFAULT;
    }
}