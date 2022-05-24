/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class ZoekParameters {
    zoeken: { [key: string]: string } = {};
    filters: { [key: string]: string } = {};
    rows: number;
    start: number;
}
