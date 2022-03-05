/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {Observable, of} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {TakenService} from '../../taken/taken.service';
import {TaakDocumentUploadFieldBuilder} from '../../shared/material-form-builder/form-components/taak-document-upload/taak-document-upload-field-builder';
import {ParagraphFormFieldBuilder} from '../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';

export class ExternAdviesVastleggen extends AbstractFormulier {

    fields = {
        VRAAG: 'vraag',
        ADVISEUR: 'adviseur',
        BRON: 'bron',
        EXTERNADVIES: 'externAdvies',
        BIJLAGE: 'bijlage'
    };

    constructor(
        translate: TranslateService,
        public takenService: TakenService,
        public informatieObjectenService: InformatieObjectenService) {
        super(translate);
    }

    _initStartForm() {
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).validators(Validators.required).build()],
            [new TextareaFormFieldBuilder().id(fields.ADVISEUR).label(fields.ADVISEUR).validators(Validators.required).build()],
            [new TextareaFormFieldBuilder().id(fields.BRON).label(fields.BRON).validators(Validators.required).build()]
        );
    }

    _initBehandelForm() {
        this.doDisablePartialSave();
        const fields = this.fields;
        this.form.push(
            [new ParagraphFormFieldBuilder().text( this.translate.instant('msg.extern.advies.vastleggen.behandelen')).build()],
            [new ReadonlyFormFieldBuilder().id(fields.VRAAG)
                                          .label(fields.VRAAG)
                                          .value(this.getDataElement(fields.VRAAG))
                                          .build()],
            [new ReadonlyFormFieldBuilder().id(fields.ADVISEUR)
                                           .label(fields.ADVISEUR)
                                           .value(this.getDataElement(fields.ADVISEUR))
                                           .build()],
            [new ReadonlyFormFieldBuilder().id(fields.BRON)
                                           .label(fields.BRON)
                                           .value(this.getDataElement(fields.BRON))
                                           .build()],
            [new TextareaFormFieldBuilder().id(fields.EXTERNADVIES)
                                           .label(fields.EXTERNADVIES)
                                           .value(this.getDataElement(fields.EXTERNADVIES))
                                           .validators(Validators.required)
                                           .readonly(this.isAfgerond()).build()]
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
                                                     .defaultTitel(this.translate.instant(fields.EXTERNADVIES))
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
