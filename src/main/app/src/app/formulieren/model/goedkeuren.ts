/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {Validators} from '@angular/forms';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {TakenService} from '../../taken/taken.service';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {ParagraphFormFieldBuilder} from '../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {RadioFormFieldBuilder} from '../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {TaakDocumentUploadFieldBuilder} from '../../shared/material-form-builder/form-components/taak-document-upload/taak-document-upload-field-builder';
import {Observable, of} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';

export class Goedkeuren extends AbstractFormulier {

    fields = {
        TOELICHTING: 'toelichtingGoedkeuring',
        VRAAG: 'vraag',
        GOEDKEUREN: 'goedkeuren',
        BIJLAGE: 'bijlage',
        RELEVANTE_DOCUMENTEN: 'relevanteDocumenten'
    };

    taakinformatieMapping = {
        uitkomst: this.fields.GOEDKEUREN,
        opmerking: this.fields.TOELICHTING,
        bijlage: this.fields.BIJLAGE
    };

    constructor(translate: TranslateService,
                public takenService: TakenService,
                public informatieObjectenService: InformatieObjectenService) {
        super(translate);
    }

    _initStartForm() {
        const zoekparameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekparameters.zaakUUID = this.zaakUuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).validators(Validators.required)
                                           .maxlength(1000)
                                           .build()],
            [new DocumentenLijstFieldBuilder().id(fields.RELEVANTE_DOCUMENTEN).label(fields.RELEVANTE_DOCUMENTEN)
                                              .documenten(documenten).build()]
        );
    }

    _initBehandelForm() {
        this.doDisablePartialSave();
        const fields = this.fields;
        const goedkeurenDataElement = this.getDataElement(fields.GOEDKEUREN);
        this.form.push(
            [new ParagraphFormFieldBuilder().text(
                this.translate.instant('msg.goedkeuring.behandelen', {zaaknummer: this.taak.zaakIdentificatie}))
                                            .build()],
            [new ReadonlyFormFieldBuilder().id(fields.VRAAG)
                                           .label(fields.VRAAG)
                                           .value(this.getDataElement(fields.VRAAG))
                                           .build()],
            [new DocumentenLijstFieldBuilder().id(fields.RELEVANTE_DOCUMENTEN)
                                              .label(fields.RELEVANTE_DOCUMENTEN)
                                              .documenten(this.getDocumenten$(fields.RELEVANTE_DOCUMENTEN))
                                              .readonly(true)
                                              .build()],
            [new RadioFormFieldBuilder().id(fields.GOEDKEUREN)
                                        .label(fields.GOEDKEUREN)
                                        .value(this.isAfgerond() && goedkeurenDataElement ?
                                            this.translate.instant(goedkeurenDataElement) : goedkeurenDataElement)
                                        .options(this.getGoedkeurenOpties())
                                        .validators(Validators.required)
                                        .readonly(this.isAfgerond())
                                        .build()],
            [new TextareaFormFieldBuilder().id(fields.TOELICHTING)
                                           .label(fields.TOELICHTING)
                                           .value(this.getDataElement(fields.TOELICHTING))
                                           .validators(Validators.required)
                                           .readonly(this.isAfgerond())
                                           .maxlength(1000)
                                           .build()]
        );
        if (this.isAfgerond()) {
            this.form.push(
                [new DocumentenLijstFieldBuilder().id(fields.BIJLAGE)
                                                  .label(fields.BIJLAGE)
                                                  .documenten(this.getDocumenten$(fields.BIJLAGE))
                                                  .readonly(true)
                                                  .build()]);
        } else {
            this.form.push(
                [new TaakDocumentUploadFieldBuilder().id(fields.BIJLAGE)
                                                     .label(fields.BIJLAGE)
                                                     .defaultTitel(this.translate.instant('goedkeuren'))
                                                     .uploadURL(
                                                         this.takenService.getUploadURL(this.taak.id, fields.BIJLAGE))
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

    getGoedkeurenOpties(): Observable<string[]> {
        return of([
            'actie.ja',
            'actie.nee'
        ]);
    }

}
