/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {Observable, of} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {TakenService} from '../../taken/taken.service';
import {RadioFormFieldBuilder} from '../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {ParagraphFormFieldBuilder} from '../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder';
import {map, mergeMap} from 'rxjs/operators';
import {ReferentieTabelWaarde} from '../../admin/model/referentie-tabel-waarde';
import {ZakenService} from '../../zaken/zaken.service';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';

export class Advies extends AbstractFormulier {

    public static formulierDefinitie = 'ADVIES';

    fields = {
        TOELICHTING: 'toelichtingAdvies',
        VRAAG: 'vraag',
        ADVIES: 'advies',
        RELEVANTE_DOCUMENTEN: 'relevanteDocumenten'
    };

    taakinformatieMapping = {
        uitkomst: this.fields.ADVIES,
        opmerking: this.fields.TOELICHTING
    };

    constructor(
        translate: TranslateService,
        public takenService: TakenService,
        public informatieObjectenService: InformatieObjectenService,
        private zakenService: ZakenService,
        private zaakafhandelParametersService: ZaakafhandelParametersService) {
        super(translate, informatieObjectenService);
    }

    _initStartForm() {
        const zoekparameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekparameters.zaakUUID = this.zaakUuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).validators(Validators.required)
                                           .maxlength(1000).build()],
            [new DocumentenLijstFieldBuilder().id(fields.RELEVANTE_DOCUMENTEN).label(fields.RELEVANTE_DOCUMENTEN)
                                              .documenten(documenten).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ParagraphFormFieldBuilder().text('msg.advies.behandelen').build()],
            [new ReadonlyFormFieldBuilder(this.getDataElement(fields.VRAAG)).id(fields.VRAAG)
                                                                            .label(fields.VRAAG)
                                                                            .build()],
            [new DocumentenLijstFieldBuilder().id(fields.RELEVANTE_DOCUMENTEN)
                                              .label(fields.RELEVANTE_DOCUMENTEN)
                                              .documenten(this.getDocumenten$(fields.RELEVANTE_DOCUMENTEN))
                                              .readonly(true)
                                              .build()],
            [new RadioFormFieldBuilder(this.getDataElement(fields.ADVIES)).id(fields.ADVIES)
                                        .label(fields.ADVIES)
                                        .options(this.getAdviesWaarden(this.zaakUuid))
                                        .validators(Validators.required)
                                        .readonly(this.readonly)
                                        .build()],
            [new TextareaFormFieldBuilder().id(fields.TOELICHTING)
                                           .label(fields.TOELICHTING)
                                           .validators(Validators.required)
                                           .readonly(this.readonly)
                                           .maxlength(1000)
                                           .build()]
        );
    }

    getDocumenten$(field: string): Observable<EnkelvoudigInformatieobject[]> {
        const dataElement = this.getDataElement(field);
        if (dataElement) {
            const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
            zoekParameters.UUIDs = dataElement.split(';');
            return this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters);
        }
        return of([]);
    }

    getAdviesWaarden(zaakUuid: string): Observable<string[]> {
        const advies: string = this.fields.ADVIES.toUpperCase();
        return this.zakenService.readZaak(zaakUuid)
                   .pipe(mergeMap(zaak => this.zaakafhandelParametersService.findReferentieTabelWaarden(zaak.zaaktype.uuid, Advies.formulierDefinitie, advies)
                                              .pipe(map((tabel: ReferentieTabelWaarde[]) => tabel.map(waarde => waarde.naam)))));
    }
}
