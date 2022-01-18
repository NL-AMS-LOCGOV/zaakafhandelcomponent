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

export class Advies extends AbstractFormulier {

    fields = {
        TOELICHTING: 'toelichting',
        VRAAG: 'vraag',
        ADVIES: 'advies',
        DOCUMENT: 'document'
    };

    constructor(translate: TranslateService) {
        super(translate);
    }

    _initStartForm() {
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).value(this.getDataElement(fields.VRAAG)).validators(Validators.required)
                                           .build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ReadonlyFormFieldBuilder().id(fields.VRAAG).label(fields.VRAAG).value(this.getDataElement(fields.VRAAG)).build()],
            [new TextareaFormFieldBuilder().id(fields.TOELICHTING).label(fields.TOELICHTING).value(this.getDataElement(fields.TOELICHTING))
                                           .readonly(this.isAfgerond()).build()],
            [new FileFormFieldBuilder().id(fields.DOCUMENT).label(fields.DOCUMENT).config(this.fileUploadConfig()).readonly(this.isAfgerond()).build()]
        );
    }

    fileUploadConfig(): FileFieldConfig {
        const uploadUrl = 'URL';
        return new FileFieldConfig(uploadUrl, [Validators.required], 1);
    }

}
