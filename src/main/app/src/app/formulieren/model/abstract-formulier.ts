/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup, Validators} from '@angular/forms';
import {Taak} from '../../taken/model/taak';
import {Groep} from '../../identity/model/groep';
import {Observable} from 'rxjs';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {TranslateService} from '@ngx-translate/core';

export abstract class AbstractFormulier {

    planItem: PlanItem;
    taak: Taak;
    dataElementen: {};
    afgerond: boolean;
    form: Array<AbstractFormField[]>;
    disablePartialSave: boolean = false;

    protected constructor(protected translate: TranslateService) {}

    initStartForm() {
        this.form = [];
        this.form.push([new HeadingFormFieldBuilder(this.translate).id('taakStarten').label(this.getStartTitel()).level('2').build()]);
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
            if (key !== 'groep') {
                dataElementen[key] = formGroup.controls[key]?.value;
            }
        });

        return dataElementen;
    }

    addGroepAssignment(groepen: Observable<Groep[]>): void {
        const groep = new SelectFormFieldBuilder(this.translate).id('groep')
                                                                .label('actie.taak.toekennen.groep')
                                                                .value(this.planItem.groep)
                                                                .optionLabel('naam').options(groepen)
                                                                .validators(Validators.required).build();
        this.form.push([groep]);
    }

    isAfgerond(): boolean {
        return this.afgerond;
    }

    doDisablePartialSave(): void {
        this.disablePartialSave = true;
    }

}
