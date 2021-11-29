/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup, Validators} from '@angular/forms';
import {Taak} from '../../taken/model/taak';
import {Groep} from '../../identity/model/groep';
import {Observable} from 'rxjs';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';

export abstract class AbstractFormulier {

    planItem: PlanItem;
    taak: Taak;
    dataElementen: {};

    abstract form: Array<AbstractFormField[]>;

    constructor() {}

    abstract initStartForm();

    abstract initBehandelForm();

    getPlanItem(formGroup: FormGroup): PlanItem {
        this.planItem.groep = formGroup.controls['groep']?.value;
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
        const dataElementen: {} = {};

        Object.keys(formGroup.controls).forEach((key) => {
            dataElementen[key] = formGroup.controls[key]?.value;
        });

        return dataElementen;
    }

    addGroepAssignment(groepen: Observable<Groep[]>): void {
        const groupForm = [
            [new HeadingFormFieldBuilder().id('taakToekenning').label('actie.toekennen').level('2').build()],
            [new SelectFormFieldBuilder().id('groep').label('groep.-kies-').value(this.planItem.groep)
                                         .optionLabel('naam').options(groepen)
                                         .validators(Validators.required).build()]
        ];

        this.form = [...this.form, ...groupForm];
    }
}
