/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ZoekObject } from "../zoek-object";
import { TaakRechten } from "../../../policy/model/taak-rechten";
import { ZoekObjectType } from "../zoek-object-type";

export class TaakZoekObject implements ZoekObject {
  id: string;
  type: ZoekObjectType;
  naam: string;
  toelichting: string;
  status: string;
  zaaktypeUuid: string;
  zaaktypeIdentificatie: string;
  zaaktypeOmschrijving: string;
  zaakUuid: string;
  zaakIdentificatie: string;
  zaakOmschrijving: string;
  zaakToelichting: string;
  creatiedatum: string;
  toekenningsdatum: string;
  fataledatum: string;
  groepID: string;
  groepNaam: string;
  behandelaarNaam: string;
  behandelaarGebruikersnaam: string;
  taakData: string[];
  taakInformatie: string[];
  rechten: TaakRechten;
}
