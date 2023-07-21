/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class InboxProductaanvraag {
  id: number;
  productaanvraagObjectUUID: string;
  aanvraagdocumentUUID: string;
  ontvangstdatum: string;
  aantalBijlagen: number;
  type: string;
  initiatorID: string;
}
