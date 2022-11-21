/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export enum MailtemplateVariabele {
    ZAAKNUMMER = 'ZAAKNUMMER',
    ZAAKTYPE = 'ZAAKTYPE',
    ZAAKSTATUS = 'ZAAKSTATUS',
    REGISTRATIEDATUM = 'REGISTRATIEDATUM',
    STARTDATUM = 'STARTDATUM',
    STREEFDATUM = 'STREEFDATUM',
    FATALEDATUM = 'FATALEDATUM',
    OMSCHRIJVINGZAAK = 'OMSCHRIJVINGZAAK',
    TOELICHTINGZAAK = 'TOELICHTINGZAAK',
    INITIATOR = 'INITIATOR',
    ADRESINITIATOR = 'ADRESINITIATOR',
    TOEGEWEZENGROEPZAAK = 'TOEGEWEZENGROEPZAAK',
    TOEGEWEZENGEBRUIKERZAAK = 'TOEGEWEZENGEBRUIKERZAAK',
    TOEGEWEZENGROEPTAAK = 'TOEGEWEZENGROEPTAAK',
    TOEGEWEZENGEBRUIKERTAAK = 'TOEGEWEZENGEBRUIKERTAAK',
    ZAAKURL = 'ZAAKURL',
    TAAKURL = 'TAAKURL'
}

export class MailtemplateVariabeleUtil {
    static getDefaultVariabelen(): MailtemplateVariabele[] {
        return [MailtemplateVariabele.ZAAKNUMMER, MailtemplateVariabele.ZAAKTYPE, MailtemplateVariabele.ZAAKSTATUS,
            MailtemplateVariabele.REGISTRATIEDATUM, MailtemplateVariabele.STARTDATUM, MailtemplateVariabele.STREEFDATUM,
            MailtemplateVariabele.FATALEDATUM, MailtemplateVariabele.OMSCHRIJVINGZAAK,
            MailtemplateVariabele.TOELICHTINGZAAK];
    }
}
