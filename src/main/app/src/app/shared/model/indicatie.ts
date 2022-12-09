/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class Indicatie {
    naam: string;
    primary: boolean;
    toelichting: string;

    constructor(naam: string, primary: boolean, toelichting: string) {
        this.naam = naam;
        this.primary = primary;
        this.toelichting = toelichting;
    }
}
