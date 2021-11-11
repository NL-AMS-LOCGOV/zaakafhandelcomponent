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
import {Taak} from '../../taken/model/taak';

export abstract class AbstractFormulier {

    modus: FormulierModus;
    planItem: PlanItem;
    taak: Taak;
    dataElementen: {};

    abstract formFieldDefinitions: Array<string>;
    abstract form: Array<AbstractFormField[]>;

    constructor() {

    }

    init() {
        if (this.modus == FormulierModus.START) {
            this.initStartForm();
        } else {
            this.initBehandelForm();
        }
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
        this.formFieldDefinitions.forEach(key => {
            dataElementen[key] = formGroup.controls[key]?.value;
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
