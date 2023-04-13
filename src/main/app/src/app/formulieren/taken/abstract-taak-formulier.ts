/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {FormGroup} from '@angular/forms';
import {Taak} from '../../taken/model/taak';
import {TranslateService} from '@ngx-translate/core';
import {TaakStuurGegevens} from '../../plan-items/model/taak-stuur-gegevens';
import {Taakinformatie} from '../../taken/model/taakinformatie';
import {HumanTaskData} from '../../plan-items/model/human-task-data';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {Observable} from 'rxjs';
import {InformatieobjectZoekParameters} from '../../informatie-objecten/model/informatieobject-zoek-parameters';
import {Zaak} from '../../zaken/model/zaak';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';

export abstract class AbstractTaakFormulier {

    public static TAAK_TOEKENNING: string = 'taakToekenning';
    protected static TAAK_FATALEDATUM: string = 'taakFataledatum';
    protected static BIJLAGEN_FIELD: string = 'bijlagen';
    protected static ONDERTEKENEN_FIELD: string = 'ondertekenen';
    protected static TAAK_DATA_MULTIPLE_VALUE_JOIN_CHARACTER = ';';

    zaak: Zaak;
    taakNaam: string;
    humanTaskData: HumanTaskData;
    taak: Taak;
    tabellen: { [key: string]: string[] };
    abstract taakinformatieMapping: { uitkomst: string, bijlagen?: string, opmerking?: string };
    dataElementen: { [key: string]: string } = {};
    readonly: boolean;
    form: AbstractFormField[][];
    disablePartialSave: boolean = false;
    taakDocumenten: EnkelvoudigInformatieobject[];

    protected constructor(protected translate: TranslateService,
                          public informatieObjectenService: InformatieObjectenService) {}

    initStartForm() {
        this.humanTaskData.taakStuurGegevens = new TaakStuurGegevens();
        this.form = [];
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

    getBehandelTitel(): string {
        if (this.readonly) {
            return this.translate.instant(`title.taak.raadplegen`, {taak: this.taak.naam});
        } else {
            return this.translate.instant(`title.taak.behandelen`, {taak: this.taak.naam});
        }
    }

    getHumanTaskData(formGroup: FormGroup): HumanTaskData {
        const values = formGroup.value;
        const toekenning = values[AbstractTaakFormulier.TAAK_TOEKENNING];
        const fataledatum = values[AbstractTaakFormulier.TAAK_FATALEDATUM];
        this.humanTaskData.medewerker = toekenning.medewerker;
        this.humanTaskData.groep = toekenning.groep;
        this.humanTaskData.fataledatum = fataledatum;
        this.humanTaskData.taakdata = this.getDataElementen(formGroup);
        return this.humanTaskData;
    }

    getTaak(formGroup: FormGroup): Taak {
        this.taak.taakdata = this.getDataElementen(formGroup);
        this.taak.taakinformatie = this.getTaakinformatie(formGroup);
        return this.taak;
    }

    protected getDataElement(key: string): any {
        return key in this.dataElementen ? this.dataElementen[key] : null;
    }

    refreshTaakdocumenten() {
        this.form.forEach((value, index) => {
            value.forEach(field => {
                if (field.id === AbstractTaakFormulier.BIJLAGEN_FIELD) {
                    this.form.splice(index, 1);
                }
            });
        });

        const taakDocumenten$ = this.getTaakdocumenten();

        this.form.push(
            [new DocumentenLijstFieldBuilder().id(AbstractTaakFormulier.BIJLAGEN_FIELD)
                                              .label(AbstractTaakFormulier.BIJLAGEN_FIELD)
                                              .documenten(taakDocumenten$)
                                              .readonly(true)
                                              .build()]);

        taakDocumenten$.subscribe(taakDocumenten => {
            this.taakDocumenten = taakDocumenten;
        });
    }

    private getTaakdocumenten(): Observable<EnkelvoudigInformatieobject[]> {
        const zoekParameters = new InformatieobjectZoekParameters();
        zoekParameters.zaakUUID = this.zaak.uuid;
        zoekParameters.informatieobjectUUIDs = [];

        if (this.taak?.taakdocumenten) {
            this.taak.taakdocumenten.forEach((uuid) => {
                zoekParameters.informatieobjectUUIDs.push(uuid);
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
        Object.entries(formGroup.value)
              .filter(([key]) => key !== AbstractTaakFormulier.TAAK_TOEKENNING)
              .filter(([key]) => key !== AbstractTaakFormulier.TAAK_FATALEDATUM)
              .filter(([key]) => !this.isReadonlyFormField(key) || key === AbstractTaakFormulier.ONDERTEKENEN_FIELD)
              .map(([key, value]) => {
                  this.dataElementen[key] = value as any;
                  if (typeof this.dataElementen[key] === 'boolean') {
                      this.dataElementen[key] = `${this.dataElementen[key]}`; // convert to string, boolean not allowed in string map (yasson/jsonb exception)
                  }
              });
        return this.dataElementen;
    }

    private isReadonlyFormField(id: string): boolean {
        for (const fieldArray of this.form) {
            for (const field of fieldArray) {
                if (field.id === id) {
                    return field.readonly;
                }
            }
        }
        return false;
    }

    private getTaakinformatie(formGroup: FormGroup): Taakinformatie {
        return {
            uitkomst: formGroup.controls[this.taakinformatieMapping.uitkomst]?.value,
            opmerking: formGroup.controls[this.taakinformatieMapping.opmerking]?.value,
            bijlagen: this.getDocumentInformatie()
        };
    }

    getFormField(id: string): AbstractFormField {
        for (const fieldArray of this.form) {
            for (const field of fieldArray) {
                if (field.id === id) {
                    return field;
                }
            }
        }
        throw new Error(`FormField: "${id}" not found!`);
    }

}
