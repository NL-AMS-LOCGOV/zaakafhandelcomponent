/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { User } from "../../identity/model/user";
import { ZaakResultaat } from "./zaak-resultaat";
import { ZaakRechten } from "../../policy/model/zaak-rechten";

export class ZaakOverzicht {
  identificatie: string;
  uuid: string;
  toelichting: string;
  omschrijving: string;
  startdatum: string;
  status: string;
  zaaktype: string;
  behandelaar: User;
  uiterlijkeDatumAfdoening: string;
  resultaat: ZaakResultaat;
  rechten: ZaakRechten;
}
