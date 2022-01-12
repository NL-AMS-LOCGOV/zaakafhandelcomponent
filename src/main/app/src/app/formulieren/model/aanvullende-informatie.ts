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

    _initStartForm() {
        this.form = [
            [new HeadingFormFieldBuilder().id('doPlanItem').label(this.getStartTitel()).level('1').build()],
            [new TextareaFormFieldBuilder().id(Fields.TOELICHTING).label(Fields.TOELICHTING)
                                           .value(this.getDataElement(Fields.TOELICHTING))
                                           .validators(Validators.required).build()]
        ];
    }

    _initBehandelForm() {
        this.form = [
            [new ReadonlyFormFieldBuilder().id(Fields.TOELICHTING).label(Fields.TOELICHTING).value(this.getDataElement(Fields.TOELICHTING)).build()],
            [new TextareaFormFieldBuilder().id(Fields.OPGEVRAAGDEINFO).label(Fields.OPGEVRAAGDEINFO).value(this.getDataElement(Fields.OPGEVRAAGDEINFO))
                                           .validators(Validators.required).readonly(this.isAfgerond()).build()],
            [new DateFormFieldBuilder().id(Fields.DATUMGEVRAAGD).label(Fields.DATUMGEVRAAGD).value(this.getDataElement(Fields.DATUMGEVRAAGD))
                                       .readonly(this.isAfgerond())
                                       .build(),
                new DateFormFieldBuilder().id(Fields.DATUMGELEVERD).label(Fields.DATUMGELEVERD).value(this.getDataElement(Fields.DATUMGELEVERD))
                                          .readonly(this.isAfgerond()).build()]
        ];
    }

    getStartTitel(): string {
        return 'Aanvullende informatie vragen';
    }

    getBehandelTitel(): string {
        return this.isAfgerond() ? 'Gegeven aanvullende informatie' : 'Aanvullende informatie geven';
    }
}

enum Fields {
    TOELICHTING = 'toelichting',
    OPGEVRAAGDEINFO = 'opgevraagdeInfo',
    DATUMGEVRAAGD = 'datumGevraagd',
    DATUMGELEVERD = 'datumGeleverd'
}
