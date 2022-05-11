/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export interface Resultaat<TYPE> {
    resultaten: TYPE[];
    totaal: number;
    foutmelding: string;
}
