/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../plan-items/model/plan-item';
import {AbstractFormulier} from './model/abstract-formulier';
import {Taak} from '../taken/model/taak';

export class FormulierBuilder {

    protected readonly _formulier: AbstractFormulier;

    constructor(formulier: AbstractFormulier) {
        this._formulier = formulier;
    }

    startForm(planItem: PlanItem): FormulierBuilder {
        this._formulier.planItem = planItem;
        this._formulier.dataElementen = planItem.taakdata;
        this._formulier.initStartForm();
        this._formulier.addGroepAssignment();
        return this;
    }

    behandelForm(taak: Taak): FormulierBuilder {
        this._formulier.taak = taak;
        this._formulier.dataElementen = taak.taakdata;
        this._formulier.initBehandelForm();
        return this;
    }

    build(): AbstractFormulier {
        return this._formulier;
    }
}
