/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export enum MailtemplateKoppelingMail {
    ZAAK_ONTVANKELIJK = 'ZAAK_ONTVANKELIJK',
    ZAAK_NIET_ONTVANKELIJK = 'ZAAK_NIET_ONTVANKELIJK',
    ZAAK_AFGEHANDELD = 'ZAAK_AFGEHANDELD',
    PROCES_AANVULLENDE_INFORMATIE = 'PROCES_AANVULLENDE_INFORMATIE',
    PROCES_ONTVANGSTBEVESTIGING = 'PROCES_ONTVANGSTBEVESTIGING',
    PROCES_ADVIES = 'PROCES_ADVIES'
}

export class MailtemplateKoppelingMailUtil {
    static getBeschikbareMailtemplateKoppelingen(): MailtemplateKoppelingMail[] {
        return [MailtemplateKoppelingMail.ZAAK_ONTVANKELIJK, MailtemplateKoppelingMail.ZAAK_NIET_ONTVANKELIJK,
            MailtemplateKoppelingMail.ZAAK_AFGEHANDELD, MailtemplateKoppelingMail.PROCES_AANVULLENDE_INFORMATIE,
            MailtemplateKoppelingMail.PROCES_ONTVANGSTBEVESTIGING, MailtemplateKoppelingMail.PROCES_ADVIES];
    }
}
