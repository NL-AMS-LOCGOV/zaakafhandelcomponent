/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export interface Results<TYPE> {
    results: TYPE[];
    count: number;
    foutmelding: string;
}
