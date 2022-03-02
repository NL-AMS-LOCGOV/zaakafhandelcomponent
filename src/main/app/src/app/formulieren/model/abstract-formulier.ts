/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup, Validators} from '@angular/forms';
import {Taak} from '../../taken/model/taak';
import {Groep} from '../../identity/model/groep';
import {Observable, of} from 'rxjs';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {AbstractChoicesFormField} from '../../shared/material-form-builder/model/abstract-choices-form-field';

export abstract class AbstractFormulier {

    planItem: PlanItem;
    taak: Taak;
    dataElementen: {};
    afgerond: boolean;
    form: Array<AbstractFormField[]>;
    disablePartialSave: boolean = false;
    groepFormField: AbstractFormField;
    filterFormField: AbstractFormField;
    medewerkerFormField: AbstractChoicesFormField;

    protected constructor(protected translate: TranslateService) {}

    initStartForm() {
        this.form = [];
        this.form.push(
            [new HeadingFormFieldBuilder().id('taakStarten').label(this.getStartTitel()).level('2').build()]);
        this._initStartForm();
    }

    initBehandelForm(afgerond: boolean) {
        this.form = [];
        this.afgerond = afgerond;
        this._initBehandelForm();
    }

    abstract _initStartForm();

    abstract _initBehandelForm();

    getStartTitel(): string {
        return this.translate.instant(`title.taak.starten`, {taak: this.planItem.naam});
    }

    getBehandelTitel(): string {
        if (this.isAfgerond()) {
            return this.translate.instant(`title.taak.raadplegen`, {taak: this.taak.naam});
        } else {
            return this.translate.instant(`title.taak.behandelen`, {taak: this.taak.naam});
        }
    }

    getPlanItem(formGroup: FormGroup): PlanItem {
        this.planItem.groep = formGroup.controls['groep']?.value;
        this.planItem.medewerker = formGroup.controls['behandelaar']?.value;
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
            if (key !== 'groep' && key !== 'behandelaar' && key !== 'filter') {
                dataElementen[key] = formGroup.controls[key]?.value;
            }
        });

        return dataElementen;
    }

    addAssignment(groepen: Observable<Groep[]>): void {
        this.groepFormField = new SelectFormFieldBuilder().id('groep')
                                                          .label('actie.taak.toekennen.groep')
                                                          .value(this.planItem.groep)
                                                          .optionLabel('naam').options(groepen)
                                                          .validators(Validators.required).build();

        this.filterFormField = new CheckboxFormFieldBuilder().id('filter')
                                                             .label('filter.groep')
                                                             .value(true)
                                                             .build();

        this.medewerkerFormField = new SelectFormFieldBuilder().id('behandelaar')
                                                               .label('actie.taak.toekennen.medewerker')
                                                               .value(this.planItem.medewerker)
                                                               .optionLabel('naam')
                                                               .options(of([]))
                                                               .build();

        this.form.push([this.groepFormField]);
        this.form.push([this.filterFormField]);
        this.form.push([this.medewerkerFormField]);
    }

    isAfgerond(): boolean {
        return this.afgerond;
    }

    doDisablePartialSave(): void {
        this.disablePartialSave = true;
    }

}
