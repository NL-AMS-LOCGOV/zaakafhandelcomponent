/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { VervalReden } from "./vervalReden";

export class BesluitIntrekkenGegevens {
  besluitUuid: string;
  vervaldatum: string;
  vervalreden: VervalReden;
  reden: string;
}
