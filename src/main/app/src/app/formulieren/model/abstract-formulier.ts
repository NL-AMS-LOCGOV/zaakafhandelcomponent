/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {SelectGroepFormField} from '../../shared/material-form-builder/form-components/select/select-groep/select-groep-form-field';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {FormGroup, Validators} from '@angular/forms';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {FormulierModus} from './formulier-modus';

export abstract class AbstractFormulier {

    private modus: FormulierModus;

    private planItem: PlanItem;
    private _dataElementen: Map<string, string>;

    abstract formFields: Array<string>;
    abstract formulier: Array<AbstractFormField[]>;

    constructor(modus: FormulierModus, planItem: PlanItem, dataElementen: Map<string, string>) {
        this.modus = modus;
        this.planItem = planItem;
        this._dataElementen = dataElementen;

        if (modus == FormulierModus.START) {
            this.initStartForm();
        } else {
            this.initBehandelForm();
        }
    }

    abstract initStartForm();

    abstract initBehandelForm();

    get dataElementen(): Map<string, string> {
        return this._dataElementen;
    }

    getPlanItem(formGroup: FormGroup): PlanItem {
        this.planItem.groep = formGroup.controls['groep'].value;
        this.planItem.taakdata = this.getDataElementen(formGroup);
        return this.planItem;
    }

    private getDataElementen(formGroup: FormGroup): Map<string, string> {
        let dataElementen: Map<string, string> = new Map<string, string>();
        this.formFields.forEach(key => {
            dataElementen.set(key, formGroup.controls[key]?.value);
        });

        return dataElementen;
    }

    protected getGroepAssignment(): Array<AbstractFormField[]> {
        return [
            [new HeadingFormField('taakToekenning', 'actie.toekennen', '2')],
            [new SelectGroepFormField(this.planItem.groep, new FormFieldConfig([Validators.required]))]
        ];
    }

}
