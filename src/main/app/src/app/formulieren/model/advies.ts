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
import {TaakDocumentUploadFieldBuilder} from '../../shared/material-form-builder/form-components/taak-document-upload/taak-document-upload-field-builder';

export class Advies extends AbstractFormulier {

    fields = {
        TOELICHTING: 'toelichtingAdvies',
        VRAAG: 'vraag',
        ADVIES: 'advies',
        BIJLAGE: 'bijlage',
        RELEVANTE_DOCUMENTEN: 'relevanteDocumenten'
    };

    constructor(
        translate: TranslateService,
        public takenService: TakenService,
        public informatieObjectenService: InformatieObjectenService) {
        super(translate);
    }

    _initStartForm() {
        const zoekparameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekparameters.zaakUUID = this.planItem.zaakUuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).validators(Validators.required).build()],
            [new DocumentenLijstFieldBuilder().id(fields.RELEVANTE_DOCUMENTEN).label(fields.RELEVANTE_DOCUMENTEN)
                                              .documenten(documenten).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ReadonlyFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).value(this.getDataElement(fields.VRAAG)).build()],
            [new DocumentenLijstFieldBuilder().id(fields.RELEVANTE_DOCUMENTEN)
                                              .label(fields.RELEVANTE_DOCUMENTEN)
                                              .documenten(this.getDocumenten$(fields.RELEVANTE_DOCUMENTEN))
                                              .readonly(true)
                                              .build()],
            [new TextareaFormFieldBuilder().id(fields.TOELICHTING).label(fields.TOELICHTING).value(this.getDataElement(fields.TOELICHTING))
                                           .readonly(this.isAfgerond()).build()]
        );
        if (this.isAfgerond()) {
            this.form.push([new DocumentenLijstFieldBuilder().id(fields.BIJLAGE)
                                                             .label(fields.BIJLAGE)
                                                             .documenten(this.getDocumenten$(fields.BIJLAGE))
                                                             .readonly(true)
                                                             .build()]);
        } else {
            this.form.push([new TaakDocumentUploadFieldBuilder().id(fields.BIJLAGE)
                                                                .label(fields.BIJLAGE)
                                                                .defaultTitel(this.translate.instant('advies'))
                                                                .uploadURL(this.takenService.getUploadURL(this.taak.id, fields.BIJLAGE))
                                                                .zaakUUID(this.taak.zaakUUID)
                                                                .readonly(this.isAfgerond())
                                                                .build()]);
        }
    }

    getDocumenten$(field: string): Observable<EnkelvoudigInformatieobject[]> {
        const dataElement = this.getDataElement(field);
        if (dataElement) {
            const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
            zoekParameters.UUIDs = dataElement.split(';');
            return this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters);
        } else {
            return of([]);
        }
    }
}
