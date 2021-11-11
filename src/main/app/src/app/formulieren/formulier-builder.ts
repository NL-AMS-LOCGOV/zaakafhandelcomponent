/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../plan-items/model/plan-item';
import {AbstractFormulier} from './model/abstract-formulier';
import {Taak} from '../taken/model/taak';
import {FormulierModus} from './model/formulier-modus';

export class FormulierBuilder {

    protected readonly _formulier: AbstractFormulier;

    constructor(formulier: AbstractFormulier) {
        this._formulier = formulier;
    }

    modus(modus: FormulierModus): FormulierBuilder {
        this._formulier.modus = modus;
        return this;
    }

    planItem(planItem: PlanItem): FormulierBuilder {
        this._formulier.planItem = planItem;
        this._formulier.dataElementen = planItem.taakdata;
        return this;
    }

    taak(taak: Taak): FormulierBuilder {
        this._formulier.taak = taak;
        this._formulier.dataElementen = taak.taakdata;
        return this;
    }

    build(): AbstractFormulier {
        this._formulier.init();

        return this._formulier;
    }
}
