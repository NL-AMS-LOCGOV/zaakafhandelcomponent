/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { SortDirection } from "@angular/material/sort";

export class ListParameters {
  sort: string;
  order: SortDirection;
  page: number;
  maxResults: number;

  constructor(sort: string, order: SortDirection) {
    this.sort = sort;
    this.order = order;
    this.page = 0;
    this.maxResults = 25;
  }
}
