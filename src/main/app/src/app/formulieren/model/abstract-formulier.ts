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
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';

export abstract class AbstractFormulier {

    public static TOEKENNING_FIELD: string = 'toekenning-field';
    public static BIJLAGEN_FIELD: string = 'bijlagen';

    zaakUuid: string;
    taakNaam: string;
    humanTaskData: HumanTaskData;
    taak: Taak;
    abstract taakinformatieMapping: { uitkomst: string, bijlagen?: string, opmerking?: string };
    dataElementen: {};
    readonly: boolean;
    form: Array<AbstractFormField[]>;
    disablePartialSave: boolean = false;
    taakDocumenten: EnkelvoudigInformatieobject[];

    protected constructor(protected translate: TranslateService,
                          public informatieObjectenService: InformatieObjectenService) {}

    initStartForm() {
        this.humanTaskData.taakStuurGegevens = new TaakStuurGegevens();
        this.form = [];
        this.form.push(
            [new HeadingFormFieldBuilder().id('taakStarten').label(this.getStartTitel()).level('2').build()]);
        this._initStartForm();
    }

    initBehandelForm(readonly: boolean) {
        this.form = [];
        this.readonly = readonly;
        this._initBehandelForm();
        this.refreshTaakdocumenten();
    }

    protected abstract _initStartForm();

    protected abstract _initBehandelForm();

    protected getStartTitel(): string {
        return this.translate.instant(`title.taak.starten`, {taak: this.taakNaam});
    }

    getBehandelTitel(): string {
        if (this.readonly) {
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

    protected getDataElement(key: string): any {
        if (this.dataElementen && this.dataElementen.hasOwnProperty(key)) {
            return this.dataElementen[key];
        }
        return null;
    }

    refreshTaakdocumenten() {
        this.form.forEach((value, index) => {
           value.forEach(field => {
               if (field.id === AbstractFormulier.BIJLAGEN_FIELD) {
                   this.form.splice(index, 1);
               }
           });
        });

        this.form.push(
            [new DocumentenLijstFieldBuilder().id(AbstractFormulier.BIJLAGEN_FIELD)
                                              .label(AbstractFormulier.BIJLAGEN_FIELD)
                                              .documenten(this.getTaakdocumenten())
                                              .ondertekend(JSON.parse(this.dataElementen['bijlagen']).ondertekend)
                                              .readonly(true)
                                              .build()]);

        this.getTaakdocumenten().subscribe(taakDocumenten => {
            this.taakDocumenten = taakDocumenten;
        });
    }

    private getTaakdocumenten(): Observable<EnkelvoudigInformatieobject[]> {
        const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekParameters.UUIDs = [];

        if (this.taak?.taakdocumenten) {
            this.taak.taakdocumenten.forEach((uuid) => {
                zoekParameters.UUIDs.push(uuid);
            });
        }

        return this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters);
    }

    private getDocumentInformatie() {
        const documentNamen: string[] = [];

        this.taakDocumenten.forEach(taakDocument => {
            documentNamen.push(taakDocument.titel);
        });

        return documentNamen.join(', ');
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
        return {
            uitkomst: formGroup.controls[this.taakinformatieMapping.uitkomst]?.value,
            opmerking: formGroup.controls[this.taakinformatieMapping.opmerking]?.value,
            bijlagen: this.getDocumentInformatie()
        };
    }
}
