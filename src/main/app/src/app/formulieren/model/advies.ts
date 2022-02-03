/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {FileFieldConfig} from '../../shared/material-form-builder/model/file-field-config';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {FileFormFieldBuilder} from '../../shared/material-form-builder/form-components/file/file-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {Observable, of} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';

export class Advies extends AbstractFormulier {

    fields = {
        TOELICHTING: 'toelichting',
        VRAAG: 'vraag',
        ADVIES: 'advies',
        DOCUMENT: 'document',
        RELEVANTE_DOCUMENTEN: 'relevanteDocumenten'
    };

    constructor(translate: TranslateService, public informatieObjectenService: InformatieObjectenService) {
        super(translate);
    }

    _initStartForm() {
        const zoekparameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekparameters.zaakUUID = this.planItem.zaakUuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder(this.translate).id(fields.VRAAG).label(fields.VRAAG).validators(Validators.required).build()],
            [new DocumentenLijstFieldBuilder(this.translate).id(fields.RELEVANTE_DOCUMENTEN).label(fields.RELEVANTE_DOCUMENTEN)
                                                            .documenten(documenten).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ReadonlyFormFieldBuilder(this.translate).id(fields.VRAAG).label(fields.VRAAG).value(this.getDataElement(fields.VRAAG)).build()],
            [new DocumentenLijstFieldBuilder(this.translate).id(fields.RELEVANTE_DOCUMENTEN)
                                                            .label(fields.RELEVANTE_DOCUMENTEN)
                                                            .documenten(this.getDocumenten$(fields.RELEVANTE_DOCUMENTEN))
                                                            .readonly(true)
                                                            .build()],
            [new TextareaFormFieldBuilder(this.translate).id(fields.TOELICHTING).label(fields.TOELICHTING).value(this.getDataElement(fields.TOELICHTING))
                                                         .readonly(this.isAfgerond()).build()],
            [new FileFormFieldBuilder(this.translate).id(fields.DOCUMENT).label(fields.DOCUMENT).config(this.fileUploadConfig()).readonly(this.isAfgerond())
                                                     .build()]
        );
    }

    fileUploadConfig(): FileFieldConfig {
        const uploadUrl = 'URL';
        return new FileFieldConfig(uploadUrl, [Validators.required], 1);
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
