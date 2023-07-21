/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { WerklijstRechten } from "../../../policy/model/werklijst-rechten";

export class TabelGegevens {
  aantalPerPagina: number;
  pageSizeOptions: number[];
  werklijstRechten: WerklijstRechten;
}
