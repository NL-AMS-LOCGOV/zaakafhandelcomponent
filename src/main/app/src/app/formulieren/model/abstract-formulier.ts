/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {FormGroup, Validators} from '@angular/forms';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {Taak} from '../../taken/model/taak';
import {Groep} from '../../identity/model/groep';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';

export abstract class AbstractFormulier {

    planItem: PlanItem;
    taak: Taak;
    dataElementen: {};

    abstract form: Array<AbstractFormField[]>;

    constructor() {

    }

    abstract initStartForm();

    abstract initBehandelForm();

    getPlanItem(formGroup: FormGroup): PlanItem {
        this.planItem.groep = formGroup.controls['groep'].value;
        this.planItem.taakdata = this.getDataElementen(formGroup);
        return this.planItem;
    }

    getTaak(formGroup: FormGroup): Taak {
        this.taak.taakdata = this.getDataElementen(formGroup);
        return this.taak;
    }

    getDataElement(key: string): any {
        if (this.dataElementen && this.dataElementen.hasOwnProperty(key)) {
            return this.dataElementen[key];
        }
        return null;
    }

    private getDataElementen(formGroup: FormGroup): {} {
        let dataElementen: {} = {};

        Object.keys(formGroup.controls).forEach((key) => {
            dataElementen[key] = formGroup.controls[key]?.value;
        });

        return dataElementen;
    }

    addGroepAssignment(groepen: Groep[]): void {
        let groupForm = [
            [new HeadingFormField('taakToekenning', 'actie.toekennen', '2')],
            [new SelectFormField('groep', 'groep.-kies-', this.planItem.groep,
                'naam', groepen, new FormFieldConfig([Validators.required]))]
        ];

        this.form = [...this.form, ...groupForm];
    }

}
