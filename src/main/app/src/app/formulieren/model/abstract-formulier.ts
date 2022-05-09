/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItem} from '../../plan-items/model/plan-item';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup} from '@angular/forms';
import {Taak} from '../../taken/model/taak';
import {Group} from '../../identity/model/group';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {TaakStuurGegevens} from '../../plan-items/model/taak-stuur-gegevens';
import {User} from '../../identity/model/user';
import {Taakinformatie} from '../../taken/model/taakinformatie';

export abstract class AbstractFormulier {

    public static TOEKENNING_FIELD: string = 'toekenning-field';

    planItem: PlanItem;
    taak: Taak;
    abstract taakinformatieMapping: { uitkomst: string, bijlage?: string, opmerking?: string };
    dataElementen: {};
    afgerond: boolean;
    form: Array<AbstractFormField[]>;
    disablePartialSave: boolean = false;

    protected constructor(protected translate: TranslateService) {}

    initStartForm() {
        this.planItem.taakStuurGegevens = new TaakStuurGegevens();
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
        const toekenning: { groep: Group, medewerker?: User } = formGroup.controls[AbstractFormulier.TOEKENNING_FIELD].value;
        this.planItem.medewerker = toekenning.medewerker;
        this.planItem.groep = toekenning.groep;
        this.planItem.taakdata = this.getDataElementen(formGroup);
        return this.planItem;
    }

    getTaak(formGroup: FormGroup): Taak {
        this.taak.taakdata = this.getDataElementen(formGroup);
        this.taak.taakinformatie = this.getTaakinformatie(formGroup);
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
            if (key !== AbstractFormulier.TOEKENNING_FIELD) {
                dataElementen[key] = formGroup.controls[key]?.value;
            }
        });
        return dataElementen;
    }

    private getTaakinformatie(formGroup: FormGroup): Taakinformatie {
        let bijlage = formGroup.controls[this.taakinformatieMapping.bijlage]?.value;
        if (bijlage) {
            bijlage = JSON.parse(bijlage).documentTitel;
        }
        return {
            uitkomst: formGroup.controls[this.taakinformatieMapping.uitkomst]?.value,
            opmerking: formGroup.controls[this.taakinformatieMapping.opmerking]?.value,
            bijlage: bijlage
        };
    }

    isAfgerond(): boolean {
        return this.afgerond;
    }

    doDisablePartialSave(): void {
        this.disablePartialSave = true;
    }
}
