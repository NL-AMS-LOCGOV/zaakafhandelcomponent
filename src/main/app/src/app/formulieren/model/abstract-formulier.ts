/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup} from '@angular/forms';
import {Taak} from '../../taken/model/taak';
import {Group} from '../../identity/model/group';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {TaakStuurGegevens} from '../../plan-items/model/taak-stuur-gegevens';
import {User} from '../../identity/model/user';
import {Taakinformatie} from '../../taken/model/taakinformatie';
import {HumanTaskData} from '../../plan-items/model/human-task-data';

export abstract class AbstractFormulier {

    public static TOEKENNING_FIELD: string = 'toekenning-field';

    zaakUuid: string;
    taakNaam: string;
    humanTaskData: HumanTaskData;
    taak: Taak;
    abstract taakinformatieMapping: { uitkomst: string, bijlage?: string, opmerking?: string };
    dataElementen: {};
    afgerond: boolean;
    form: Array<AbstractFormField[]>;
    disablePartialSave: boolean = false;

    protected constructor(protected translate: TranslateService) {}

    initStartForm() {
        this.humanTaskData.taakStuurGegevens = new TaakStuurGegevens();
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
        return this.translate.instant(`title.taak.starten`, {taak: this.taakNaam});
    }

    getBehandelTitel(): string {
        if (this.isAfgerond()) {
            return this.translate.instant(`title.taak.raadplegen`, {taak: this.taak.naam});
        } else {
            return this.translate.instant(`title.taak.behandelen`, {taak: this.taak.naam});
        }
    }

    getHumanTaskData(formGroup: FormGroup): HumanTaskData {
        const toekenning: { groep: Group, medewerker?: User } = formGroup.controls[AbstractFormulier.TOEKENNING_FIELD].value;
        this.humanTaskData.medewerker = toekenning.medewerker;
        this.humanTaskData.groep = toekenning.groep;
        this.humanTaskData.taakdata = this.getDataElementen(formGroup);
        return this.humanTaskData;
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
