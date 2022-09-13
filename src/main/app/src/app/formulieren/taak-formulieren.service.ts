/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
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
import {Goedkeuren} from './model/goedkeuren';
import {ExternAdviesVastleggen} from './model/extern-advies-vastleggen';
import {ZakenService} from '../zaken/zaken.service';
import {ZaakafhandelParametersService} from '../admin/zaakafhandel-parameters.service';

@Injectable({
    providedIn: 'root'
})
export class TaakFormulierenService {

    constructor(private translate: TranslateService,
                private informatieObjectenService: InformatieObjectenService,
                private takenService: TakenService,
                private zakenService: ZakenService,
                private zaakafhandelParametersService: ZaakafhandelParametersService) { }

    public getFormulierBuilder(formulierNaam: string): FormulierBuilder {
        switch (formulierNaam) {
            case AanvullendeInformatie.formulierDefinitie:
                return new FormulierBuilder(
                    new AanvullendeInformatie(this.translate, this.takenService, this.informatieObjectenService));
            case Advies.formulierDefinitie:
                return new FormulierBuilder(
                    new Advies(this.translate, this.takenService, this.informatieObjectenService, this.zakenService, this.zaakafhandelParametersService));
            case ExternAdviesVastleggen.formulierDefinitie:
                return new FormulierBuilder(
                    new ExternAdviesVastleggen(this.translate, this.takenService, this.informatieObjectenService));
            case Goedkeuren.formulierDefinitie:
                return new FormulierBuilder(
                    new Goedkeuren(this.translate, this.takenService, this.informatieObjectenService));
            case DefaultTaakformulier.formulierDefinitie:
                return new FormulierBuilder(
                    new DefaultTaakformulier(this.translate, this.informatieObjectenService));
            default:
                throw new Error(`Onbekend formulier: ${formulierNaam}`);
        }
    }
}
