/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {HeadingFormFieldBuilder} from '../../shared/material-form-builder/form-components/heading/heading-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';

export class AanvullendeInformatie extends AbstractFormulier {

    form: Array<AbstractFormField[]>;

    constructor() {
        super();
    }

    initStartForm() {
        this.form = [
            [new HeadingFormFieldBuilder().id('doPlanItem').label('actie.taak.aanmaken').level('1').build()],
            [new TextareaFormFieldBuilder().id(Fields.TOELICHTING).label(Fields.TOELICHTING)
                                           .value(this.getDataElement(Fields.TOELICHTING))
                                           .validators(Validators.required).build()]
        ];
    }

    initBehandelForm(afgerond: boolean) {
        this.form = [
            [new ReadonlyFormFieldBuilder().id(Fields.TOELICHTING).label(Fields.TOELICHTING).value(this.getDataElement(Fields.TOELICHTING)).build()],
            [new TextareaFormFieldBuilder().id(Fields.OPGEVRAAGDEINFO).label(Fields.OPGEVRAAGDEINFO).value(this.getDataElement(Fields.OPGEVRAAGDEINFO))
                                           .validators(Validators.required).readonly(afgerond).build()],
            [new DateFormFieldBuilder().id(Fields.DATUMGEVRAAGD).label(Fields.DATUMGEVRAAGD).value(this.getDataElement(Fields.DATUMGEVRAAGD)).readonly(afgerond)
                                       .build(),
                new DateFormFieldBuilder().id(Fields.DATUMGELEVERD).label(Fields.DATUMGELEVERD).value(this.getDataElement(Fields.DATUMGELEVERD))
                                          .readonly(afgerond).build()]
        ];
    }

}

enum Fields {
    TOELICHTING = 'toelichting',
    OPGEVRAAGDEINFO = 'opgevraagdeInfo',
    DATUMGEVRAAGD = 'datumGevraagd',
    DATUMGELEVERD = 'datumGeleverd'
}
