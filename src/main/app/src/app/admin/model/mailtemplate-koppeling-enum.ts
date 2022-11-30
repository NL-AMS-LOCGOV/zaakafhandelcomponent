/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export enum MailtemplateKoppelingEnum {
    ZAAK_ONTVANKELIJK = 'ZAAK_ONTVANKELIJK',
    ZAAK_NIET_ONTVANKELIJK = 'ZAAK_NIET_ONTVANKELIJK',
    ZAAK_AFGEHANDELD = 'ZAAK_AFGEHANDELD',
    PROCES_AANVULLENDE_INFORMATIE = 'PROCES_AANVULLENDE_INFORMATIE',
    PROCES_ONTVANGSTBEVESTIGING = 'PROCES_ONTVANGSTBEVESTIGING',
    PROCES_ADVIES = 'PROCES_ADVIES'
}

export class MailtemplateKoppelingEnumUtil {
    static getBeschikbareMailtemplateKoppelingen(): MailtemplateKoppelingEnum[] {
        return [MailtemplateKoppelingEnum.ZAAK_ONTVANKELIJK, MailtemplateKoppelingEnum.ZAAK_NIET_ONTVANKELIJK,
            MailtemplateKoppelingEnum.ZAAK_AFGEHANDELD, MailtemplateKoppelingEnum.PROCES_AANVULLENDE_INFORMATIE,
            MailtemplateKoppelingEnum.PROCES_ONTVANGSTBEVESTIGING, MailtemplateKoppelingEnum.PROCES_ADVIES];
    }
}
