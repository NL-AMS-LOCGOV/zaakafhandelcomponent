/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SortDirection } from "@angular/material/sort";
import { ListParameters } from "../../shared/model/list-parameters";
import { DatumRange } from "../../zoeken/model/datum-range";
import { ZoekFilters } from "../../gebruikersvoorkeuren/zoekopdracht/zoekopdracht.component";

export class InboxDocumentListParameters
  extends ListParameters
  implements ZoekFilters
{
  readonly filtersType = "InboxDocumentListParameters";
  identificatie: string;
  creatiedatum = new DatumRange();
  titel: string;

  constructor(sort: string, order: SortDirection) {
    super(sort, order);
  }

  static heeftActieveFilters(zoekFilters: any): boolean {
    if (zoekFilters.identificatie != null) {
      return true;
    }
    if (
      zoekFilters.creatiedatum?.van != null ||
      zoekFilters.creatiedatum?.tot != null
    ) {
      return true;
    }
    if (zoekFilters.titel != null) {
      return true;
    }
    return false;
  }
}
