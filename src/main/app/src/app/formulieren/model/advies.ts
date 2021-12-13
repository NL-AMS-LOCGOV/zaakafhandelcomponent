/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {Validators} from '@angular/forms';
import {FileFieldConfig} from '../../shared/material-form-builder/model/file-field-config';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {FileFormFieldBuilder} from '../../shared/material-form-builder/form-components/file/file-form-field-builder';

export class Advies extends AbstractFormulier {
    form: Array<AbstractFormField[]>;

    constructor() {
        super();
    }

    initStartForm() {
        this.form = [
            [new HeadingFormFieldBuilder().id('doPlanItem').label('actie.taak.aanmaken').level('1').build()],
            [new TextareaFormFieldBuilder().id(Fields.VRAAG).label(Fields.VRAAG).value(this.getDataElement(Fields.VRAAG))
                                           .validators(Validators.required).build()]
        ];
    }

    initBehandelForm(afgerond: boolean) {
        this.form = [
            [new ReadonlyFormFieldBuilder().id(Fields.VRAAG).label(Fields.VRAAG).value(this.getDataElement(Fields.VRAAG)).build()],
            [new TextareaFormFieldBuilder().id(Fields.TOELICHTING).label(Fields.TOELICHTING).value(this.getDataElement(Fields.TOELICHTING)).readonly(afgerond)
                                           .build()],
            [new FileFormFieldBuilder().id(Fields.BIJLAGE).label(Fields.BIJLAGE).config(this.fileUploadConfig()).readonly(afgerond).build()]
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
