/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export interface TableRequest {
  pagination: Pagination;
  search?: Search;
  sort?: Sort;
}

export interface Pagination {
  pageNumber: number;
  pageSize: number;
}

export interface Search {
  predicateObject?: {};
}

export interface Sort {
  predicate?: string;
  direction?: string;
}
