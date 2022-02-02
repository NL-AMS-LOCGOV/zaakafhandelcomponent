/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {AanvullendeInformatie} from './model/aanvullende-informatie';
import {FormulierBuilder} from './formulier-builder';
import {Advies} from './model/advies';
import {DefaultTaakformulier} from './model/default-taakformulier';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectenService} from '../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../taken/taken.service';

@Injectable({
    providedIn: 'root'
})
export class TaakFormulierenService {

    constructor(private translate: TranslateService,
                private informatieObjectenService: InformatieObjectenService,
                private takenService: TakenService) { }

    public getFormulierBuilder(formulierNaam: string): FormulierBuilder {
        switch (formulierNaam) {
            case 'AANVULLENDE_INFORMATIE':
                return new FormulierBuilder(new AanvullendeInformatie(this.translate));
            case 'ADVIES':
                return new FormulierBuilder(new Advies(this.translate, this.takenService, this.informatieObjectenService));
            case 'DEFAULT_TAAKFORMULIER':
                return new FormulierBuilder(new DefaultTaakformulier(this.translate));
            default:
                throw new Error(`Onbekend formulier: ${formulierNaam}`);
        }
    }
}
