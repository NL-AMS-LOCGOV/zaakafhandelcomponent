/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {TextareaFormField} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {Validators} from '@angular/forms';
import {ReadonlyFormField} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field';
import {FileFormField} from '../../shared/material-form-builder/form-components/file/file-form-field';
import {FileFieldConfig} from '../../shared/material-form-builder/model/file-field-config';

export class Advies extends AbstractFormulier {
    form: Array<AbstractFormField[]>;
    formFieldDefinitions: Array<string>;

    constructor() {
        super();
    }

    initStartForm() {
        this.formFieldDefinitions = [Fields.VRAAG];

        this.form = [
            [new HeadingFormField('doPlanItem', 'actie.taak.aanmaken', '1')],
            [new TextareaFormField(Fields.VRAAG, Fields.VRAAG, this.getDataElement(Fields.VRAAG),
                new FormFieldConfig([Validators.required]))],
            ...this.getGroepAssignment()
        ];
    }

    initBehandelForm() {
        this.formFieldDefinitions = [Fields.ADVIES, Fields.TOELICHTING, Fields.BIJLAGE];

        this.form = [
            [new ReadonlyFormField(Fields.VRAAG, Fields.VRAAG, this.getDataElement(Fields.VRAAG))],
            // [new RadioFormField(Fields.ADVIES, Fields.ADVIES, this.getDataElement(Fields.ADVIES),
            //     new FormFieldConfig([Validators.required]))],
            [new TextareaFormField(Fields.TOELICHTING, Fields.TOELICHTING, this.getDataElement(Fields.TOELICHTING))],
            [new FileFormField(Fields.BIJLAGE, Fields.BIJLAGE, null, this.fileUploadConfig())]
        ];
    }

    fileUploadConfig(): FileFieldConfig {
        const uploadUrl = 'URL';
        return new FileFieldConfig(uploadUrl, [Validators.required], 1);
    }

}

enum Fields {
    TOELICHTING = 'toelichting',
    VRAAG = 'vraag',
    ADVIES = 'advies',
    BIJLAGE = 'BIJLAGE'
}
