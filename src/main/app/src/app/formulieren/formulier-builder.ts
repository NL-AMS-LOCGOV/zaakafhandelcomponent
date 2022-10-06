/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../plan-items/model/plan-item';
import {AbstractFormulier} from './model/abstract-formulier';
import {Taak} from '../taken/model/taak';
import {MedewerkerGroepFieldBuilder} from '../shared/material-form-builder/form-components/select-medewerker/medewerker-groep-field-builder';
import {HumanTaskData} from '../plan-items/model/human-task-data';
import {DividerFormFieldBuilder} from '../shared/material-form-builder/form-components/divider/divider-form-field-builder';
import {TaakStatus} from '../taken/model/taak-status.enum';

export class FormulierBuilder {

    protected readonly _formulier: AbstractFormulier;

    constructor(formulier: AbstractFormulier) {
        this._formulier = formulier;
    }

    startForm(planItem: PlanItem): FormulierBuilder {
        this._formulier.zaakUuid = planItem.zaakUuid;
        this._formulier.taakNaam = planItem.naam;
        this._formulier.humanTaskData = new HumanTaskData();
        this._formulier.humanTaskData.planItemInstanceId = planItem.id;
        this._formulier.initStartForm();
        this._formulier.form.push(
            [new DividerFormFieldBuilder().build()],
            [new MedewerkerGroepFieldBuilder().id(AbstractFormulier.TOEKENNING_FIELD)
                                              .label('actie.taak.toewijzing')
                                              .groepLabel('actie.taak.toekennen.groep')
                                              .medewerkerLabel('actie.taak.toekennen.medewerker')
                                              .defaultGroep(planItem.groep)
                                              .build()]);
        return this;
    }

    behandelForm(taak: Taak): FormulierBuilder {
        this._formulier.zaakUuid = taak.zaakUuid;
        this._formulier.taak = taak;
        this._formulier.dataElementen = taak.taakdata;
        this._formulier.initBehandelForm(taak.status === TaakStatus.Afgerond || !taak.rechten.wijzigenFormulier);
        return this;
    }

    build(): AbstractFormulier {
        return this._formulier;
    }
}
