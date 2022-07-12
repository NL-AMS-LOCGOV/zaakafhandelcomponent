/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class AppActies {
    aanmakenZaak: boolean;
    beheren: boolean;
    zoeken: boolean;
    zaken: boolean;
    taken: boolean;
    documenten: boolean;

    constructor() {
        this.aanmakenZaak = false;
        this.beheren = false;
        this.zoeken = false;
        this.zaken = false;
        this.taken = false;
        this.documenten = false;
    }
}
