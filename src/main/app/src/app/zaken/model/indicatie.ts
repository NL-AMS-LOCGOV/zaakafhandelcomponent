/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class Indicatie {
    constructor(indicatie: string, toelichting: string, color: string = 'accent') {
        this.color = color;
        this.naam = indicatie;
        this.info = toelichting;
    }

    color: string;
    naam: string;
    info: string;
}
