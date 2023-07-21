/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { DatumVeld } from "./datum-veld";
import { ZoekVeld } from "./zoek-veld";
import { FilterVeld } from "./filter-veld";
import { SorteerVeld } from "./sorteer-veld";
import { ZoekObjectType } from "./zoek-object-type";
import { DatumRange } from "./datum-range";
import { FilterParameters } from "./filter-parameters";
import { ZoekFilters } from "../../gebruikersvoorkeuren/zoekopdracht/zoekopdracht.component";

export class ZoekParameters implements ZoekFilters {
  readonly filtersType = "ZoekParameters";
  type: ZoekObjectType;
  alleenMijnZaken = false;
  alleenOpenstaandeZaken = false;
  alleenAfgeslotenZaken = false;
  alleenMijnTaken = false;
  zoeken: Partial<Record<ZoekVeld, string>> = {};
  filters: Partial<Record<FilterVeld, FilterParameters>> = {};
  datums: Partial<Record<DatumVeld, DatumRange>> = {};
  sorteerVeld: SorteerVeld;
  sorteerRichting: "desc" | "asc" | "";
  rows = 25;
  page = 0;

  static heeftActieveFilters(zoekFilters: any): boolean {
    if (zoekFilters.zoeken) {
      for (const field in zoekFilters.zoeken) {
        if (zoekFilters.zoeken.hasOwnProperty(field)) {
          if (zoekFilters.zoeken[field] != null) {
            return true;
          }
        }
      }
    }
    if (zoekFilters.filters) {
      for (const field in zoekFilters.filters) {
        if (zoekFilters.filters.hasOwnProperty(field)) {
          if (0 < zoekFilters.filters[field]?.waarden?.length) {
            return true;
          }
        }
      }
    }
    if (zoekFilters.datums) {
      for (const field in zoekFilters.datums) {
        if (zoekFilters.datums.hasOwnProperty(field)) {
          const datum: DatumRange = zoekFilters.datums[field];
          if (datum?.van != null || datum?.tot != null) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
