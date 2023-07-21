/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SortDirection } from "@angular/material/sort";
import { ListParameters } from "../../shared/model/list-parameters";
import { Zaaktype } from "../../zaken/model/zaaktype";
import { CaseDefinition } from "../model/case-definition";
import { ToggleSwitchOptions } from "../../shared/table-zoek-filters/toggle-filter/toggle-switch-options";
import { DatumRange } from "../../zoeken/model/datum-range";

export class ZaakafhandelParametersListParameters extends ListParameters {
  valide: ToggleSwitchOptions = ToggleSwitchOptions.INDETERMINATE;
  geldig: ToggleSwitchOptions = ToggleSwitchOptions.INDETERMINATE;
  zaaktype: Zaaktype = null;
  caseDefinition: CaseDefinition = null;
  beginGeldigheid = new DatumRange();
  eindeGeldigheid = new DatumRange();

  constructor(sort: string, order: SortDirection) {
    super(sort, order);
  }
}
