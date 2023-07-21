/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class Notitie {
  id: number;
  zaakUUID: string;
  tekst: string;
  tijdstipLaatsteWijziging: string;
  gebruikersnaamMedewerker: string;
  voornaamAchternaamMedewerker: string;
  bewerkenToegestaan: boolean;
}
