/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class TaakVerdelenGegevens {
  taken: { taakId: string; zaakUuid: string }[];
  behandelaarGebruikersnaam: string;
  groepId: string;
  reden: string;
}
