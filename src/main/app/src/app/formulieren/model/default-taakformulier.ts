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

export class DefaultTaakformulier extends AbstractFormulier {

    form: Array<AbstractFormField[]>;

    constructor() {
        super();
    }

    _initStartForm() {
        this.form = [
            [new HeadingFormFieldBuilder().id('defaultTaakFormulier').label(this.getStartTitel()).level('1').build()],
            [new TextareaFormFieldBuilder().id(Fields.REDEN_START).label(Fields.REDEN_START)
                                           .value(this.getDataElement(Fields.REDEN_START))
                                           .validators(Validators.required).build()]
        ];
    }

    _initBehandelForm() {
        this.form = [
            [new ReadonlyFormFieldBuilder().id(Fields.REDEN_START).label(Fields.REDEN_START).value(this.getDataElement(Fields.REDEN_START)).build()],
            [new TextareaFormFieldBuilder().id(Fields.AFHANDELING).label(Fields.AFHANDELING).value(this.getDataElement(Fields.AFHANDELING))
                                           .validators(Validators.required).readonly(this.isAfgerond()).build()]
        ];
    }

    getStartTitel(): string {
        return 'Taak Startformulier';
    }

    getBehandelTitel(): string {
        return this.isAfgerond() ? 'Formuliergegevens' : 'Taak afhandelden';
    }
}

enum Fields {
    REDEN_START = 'redenStart',
    AFHANDELING = 'afhandeling',

}
