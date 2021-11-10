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
import {FormulierModus} from './formulier-modus';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

export class AanvullendeInformatie extends AbstractFormulier {

    formFieldDefinitions: Array<string>;
    form: Array<AbstractFormField[]>;

    constructor(modus: FormulierModus) {
        super(modus);
    }

    initStartForm() {
        this.formFieldDefinitions = [AIFields.TOELICHTING];

        this.form = [
            [new HeadingFormField('doPlanItem', 'actie.taak.aanmaken', '1')],
            [new TextareaFormField(AIFields.TOELICHTING, AIFields.TOELICHTING, this.getDataElement(AIFields.TOELICHTING),
                new FormFieldConfig([Validators.required]))],
            ...this.getGroepAssignment()
        ];
    }

    initBehandelForm() {
        this.formFieldDefinitions = [AIFields.OPGEVRAAGDEINFO, AIFields.DATUMGEVRAAGD, AIFields.DATUMGELEVERD];

        this.form = [
            [new ReadonlyFormField(AIFields.TOELICHTING, AIFields.TOELICHTING, this.getDataElement(AIFields.TOELICHTING))],
            [new TextareaFormField(AIFields.OPGEVRAAGDEINFO, AIFields.OPGEVRAAGDEINFO, this.getDataElement(AIFields.OPGEVRAAGDEINFO),
                new FormFieldConfig([Validators.required]))],
            [new DateFormField(AIFields.DATUMGEVRAAGD, AIFields.DATUMGEVRAAGD, null),
                new DateFormField(AIFields.DATUMGELEVERD, AIFields.DATUMGELEVERD, null)]
        ];
    }

}

enum AIFields {
    TOELICHTING = 'toelichting',
    OPGEVRAAGDEINFO = 'opgevraagdeInfo',
    DATUMGEVRAAGD = 'datumGevraagd',
    DATUMGELEVERD = 'datumGeleverd'
}
