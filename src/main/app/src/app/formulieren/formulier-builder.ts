/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../plan-items/model/plan-item';
import {AbstractFormulier} from './model/abstract-formulier';
import {Taak} from '../taken/model/taak';
import {TaakStatus} from '../taken/model/taak-status.enum';
import {MedewerkerGroepFieldBuilder} from '../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-field-builder';

export class FormulierBuilder {

    protected readonly _formulier: AbstractFormulier;

    constructor(formulier: AbstractFormulier) {
        this._formulier = formulier;
    }

    startForm(planItem: PlanItem): FormulierBuilder {
        this._formulier.planItem = planItem;
        this._formulier.dataElementen = planItem.taakdata;
        this._formulier.initStartForm();
        this._formulier.form.push(
            [new MedewerkerGroepFieldBuilder().id(AbstractFormulier.TOEKENNING_FIELD)
                                              .defaultMedewerker(planItem.medewerker)
                                              .defaultGroep(planItem.groep)
                                              .build()]);
        return this;
    }

    behandelForm(taak: Taak): FormulierBuilder {
        this._formulier.taak = taak;
        this._formulier.dataElementen = taak.taakdata;
        this._formulier.initBehandelForm(TaakStatus.Afgerond === taak.status);
        return this;
    }

    build(): AbstractFormulier {
        return this._formulier;
    }
}
