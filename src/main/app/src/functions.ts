/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import * as moment from "moment";

/**
 * toJSON aanpassen zodat de huidige tijdzone wordt megeven ipv UTC
 * voor: 2021-06-17T13:43:56.111111Z
 * na:  2021-06-17T11:43:56.111111+02:00
 */
export function alterMomentToJSON(): void {
  moment.fn.toJSON = function () {
    return this.format();
  };
}
