/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {TranslateService} from '@ngx-translate/core';

export class AanvullendeInformatie extends AbstractFormulier {

    fields = {
        TOELICHTING: 'toelichting',
        OPGEVRAAGDEINFO: 'opgevraagdeInfo',
        DATUMGEVRAAGD: 'datumGevraagd',
        DATUMGELEVERD: 'datumGeleverd'
    };

    constructor(translate: TranslateService) {
        super(translate);
    }

    _initStartForm() {
        const fields = this.fields;
        this.form.push(
            [new TextareaFormFieldBuilder(this.translate).id(fields.TOELICHTING).label(fields.TOELICHTING).value(this.getDataElement(fields.TOELICHTING))
                                                         .validators(Validators.required).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ReadonlyFormFieldBuilder(this.translate).id(fields.TOELICHTING).label(fields.TOELICHTING).value(this.getDataElement(fields.TOELICHTING))
                                                         .build()],
            [new TextareaFormFieldBuilder(this.translate).id(fields.OPGEVRAAGDEINFO).label(fields.OPGEVRAAGDEINFO)
                                                         .value(this.getDataElement(fields.OPGEVRAAGDEINFO))
                                                         .validators(Validators.required).readonly(this.isAfgerond()).build()],
            [
                new DateFormFieldBuilder(this.translate).id(fields.DATUMGEVRAAGD).label(fields.DATUMGEVRAAGD).value(this.getDataElement(fields.DATUMGEVRAAGD))
                                                        .readonly(this.isAfgerond()).build(),
                new DateFormFieldBuilder(this.translate).id(fields.DATUMGELEVERD).label(fields.DATUMGELEVERD).value(this.getDataElement(fields.DATUMGELEVERD))
                                                        .readonly(this.isAfgerond()).build()
            ]
        );
    }

    getStartTitel(): string {
        return this.translate.instant('title.taak.aanvullende-informatie.starten');
    }

    getBehandelTitel(): string {
        if (this.isAfgerond()) {
            return this.translate.instant('title.taak.aanvullende-informatie.raadplegen');
        } else {
            return this.translate.instant('title.taak.aanvullende-informatie.behandelen');
        }
    }
}

