/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export interface TableResponse<RESTOBJECT> {
  data: RESTOBJECT[];
  totalItems: number;
}
