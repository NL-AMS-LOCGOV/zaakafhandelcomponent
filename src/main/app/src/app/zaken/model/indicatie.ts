/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ThemePalette} from '@angular/material/core';

export class Indicatie {
    constructor(indicatie: string, toelichting: string, color: ThemePalette = 'accent') {
        this.color = color;
        this.naam = indicatie;
        this.info = toelichting;
    }

    color: ThemePalette;
    naam: string;
    info: string;
}
