/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';

export class DefaultTaakformulier extends AbstractFormulier {

    fields = {
        REDEN_START: 'redenStart',
        AFHANDELING: 'afhandeling'
    };

    taakinformatieMapping = {
        uitkomst: this.fields.AFHANDELING
    };

    constructor(translate: TranslateService, public informatieObjectenService: InformatieObjectenService) {
        super(translate, informatieObjectenService);
    }

    _initStartForm() {
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder().id(fields.REDEN_START).label(fields.REDEN_START)
                                           .value(this.getDataElement(fields.REDEN_START))
                                           .validators(Validators.required).maxlength(1000).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ReadonlyFormFieldBuilder().id(fields.REDEN_START).label(fields.REDEN_START)
                                           .value(this.getDataElement(fields.REDEN_START))
                                           .build()],
            [new TextareaFormFieldBuilder().id(fields.AFHANDELING).label(fields.AFHANDELING)
                                           .value(this.getDataElement(fields.AFHANDELING))
                                           .validators(Validators.required).readonly(this.readonly).maxlength(1000)
                                           .build()]
        );
    }
}
