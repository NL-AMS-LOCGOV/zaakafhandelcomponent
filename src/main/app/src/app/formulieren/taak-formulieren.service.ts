/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {AanvullendeInformatie} from './model/aanvullende-informatie';
import {FormulierBuilder} from './formulier-builder';
import {Advies} from './model/advies';
import {DefaultTaakformulier} from './model/default-taakformulier';

@Injectable({
    providedIn: 'root'
})
export class TaakFormulierenService {

    constructor() { }

    public getFormulierBuilder(formulierNaam: string): FormulierBuilder {
        switch (formulierNaam) {
            case 'AANVULLENDE_INFORMATIE':
                return new FormulierBuilder(new AanvullendeInformatie());
            case 'ADVIES':
                return new FormulierBuilder(new Advies());
            case 'DEFAULT_TAAKFORMULIER':
                return new FormulierBuilder(new DefaultTaakformulier());
            default:
                throw `Onbekend formulier: ${formulierNaam}`;
        }
    }
}
