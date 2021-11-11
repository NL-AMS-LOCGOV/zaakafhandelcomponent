/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {AanvullendeInformatie} from './model/aanvullende-informatie';
import {FormulierBuilder} from './formulier-builder';
import {Advies} from './model/advies';

@Injectable({
    providedIn: 'root'
})
export class TaakFormulierenService {

    constructor() { }

    public getFormulierBuilder(formulierNaam: string): FormulierBuilder {
        let formBuilder: FormulierBuilder;
        switch (formulierNaam) {
            case 'AANVULLENDE_INFORMATIE':
                formBuilder = new FormulierBuilder(new AanvullendeInformatie());
                break;
            case 'ADVIES':
                formBuilder = new FormulierBuilder(new Advies());
                break;
            default:
                formBuilder = new FormulierBuilder(new AanvullendeInformatie());
            // throw `Onbekend formulier: ${formulierNaam}`;
        }

        return formBuilder;
    }

}
