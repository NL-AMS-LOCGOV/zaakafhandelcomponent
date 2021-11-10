/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {SelectGroepFormField} from '../../shared/material-form-builder/form-components/select/select-groep/select-groep-form-field';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {Validators} from '@angular/forms';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';

export abstract class AbstractFormulier {

    private _planItem: PlanItem;

    private _dataElementen: Map<string, string>;
    private _taakStartFormulier: Array<AbstractFormField[]>;
    private _taakBehandelFormulier: Array<AbstractFormField[]>;

    constructor(planItem: PlanItem, dataElementen: Map<string, string>) {
        this._planItem = planItem;
        this._dataElementen = dataElementen;
    }

    get taakStartFormulier(): Array<AbstractFormField[]> {
        return this._taakStartFormulier;
    }

    set taakStartFormulier(value: Array<AbstractFormField[]>) {
        this._taakStartFormulier = value;
    }

    get taakBehandelFormulier(): Array<AbstractFormField[]> {
        return this._taakBehandelFormulier;
    }

    set taakBehandelFormulier(value: Array<AbstractFormField[]>) {
        this._taakBehandelFormulier = value;
    }

    get dataElementen(): Map<string, string> {
        return this._dataElementen;
    }

    get planItem(): PlanItem {
        return this._planItem;
    }

    getGroepAssignment(): Array<AbstractFormField[]> {
        return [
            [new HeadingFormField('taakToekenning', 'actie.toekennen', '2')],
            [new SelectGroepFormField(this.planItem.groep, new FormFieldConfig([Validators.required]))]
        ];
    }

}
