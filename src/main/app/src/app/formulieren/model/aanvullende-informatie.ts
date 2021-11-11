/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {TextareaFormField} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field';
import {DateFormField} from '../../shared/material-form-builder/form-components/date/date-form-field';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';
import {Validators} from '@angular/forms';
import {ReadonlyFormField} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

export class AanvullendeInformatie extends AbstractFormulier {

    form: Array<AbstractFormField[]>;

    constructor() {
        super();
    }

    initStartForm() {
        this.form = [
            [new HeadingFormField('doPlanItem', 'actie.taak.aanmaken', '1')],
            [new TextareaFormField(Fields.TOELICHTING, Fields.TOELICHTING, this.getDataElement(Fields.TOELICHTING),
                new FormFieldConfig([Validators.required]))]
        ];
    }

    initBehandelForm() {
        this.form = [
            [new ReadonlyFormField(Fields.TOELICHTING, Fields.TOELICHTING, this.getDataElement(Fields.TOELICHTING))],
            [new TextareaFormField(Fields.OPGEVRAAGDEINFO, Fields.OPGEVRAAGDEINFO, this.getDataElement(Fields.OPGEVRAAGDEINFO),
                new FormFieldConfig([Validators.required]))],
            [new DateFormField(Fields.DATUMGEVRAAGD, Fields.DATUMGEVRAAGD, null),
                new DateFormField(Fields.DATUMGELEVERD, Fields.DATUMGELEVERD, null)]
        ];
    }

}

enum Fields {
    TOELICHTING = 'toelichting',
    OPGEVRAAGDEINFO = 'opgevraagdeInfo',
    DATUMGEVRAAGD = 'datumGevraagd',
    DATUMGELEVERD = 'datumGeleverd'
}
