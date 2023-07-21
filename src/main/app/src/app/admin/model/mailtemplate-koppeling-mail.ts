/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export enum MailtemplateKoppelingMail {
  ZAAK_ALGEMEEN = "ZAAK_ALGEMEEN",
  ZAAK_ONTVANKELIJK = "ZAAK_ONTVANKELIJK",
  ZAAK_NIET_ONTVANKELIJK = "ZAAK_NIET_ONTVANKELIJK",
  ZAAK_AFGEHANDELD = "ZAAK_AFGEHANDELD",
  TAAK_AANVULLENDE_INFORMATIE = "TAAK_AANVULLENDE_INFORMATIE",
  TAAK_ONTVANGSTBEVESTIGING = "TAAK_ONTVANGSTBEVESTIGING",
  TAAK_ADVIES_EXTERN = "TAAK_ADVIES_EXTERN",
}

export class MailtemplateKoppelingMailUtil {
  static getBeschikbareMailtemplateKoppelingen(): MailtemplateKoppelingMail[] {
    return [
      MailtemplateKoppelingMail.ZAAK_ONTVANKELIJK,
      MailtemplateKoppelingMail.ZAAK_NIET_ONTVANKELIJK,
      MailtemplateKoppelingMail.ZAAK_AFGEHANDELD,
      MailtemplateKoppelingMail.TAAK_AANVULLENDE_INFORMATIE,
      MailtemplateKoppelingMail.TAAK_ONTVANGSTBEVESTIGING,
      MailtemplateKoppelingMail.TAAK_ADVIES_EXTERN,
      MailtemplateKoppelingMail.ZAAK_ALGEMEEN,
    ];
  }
}
