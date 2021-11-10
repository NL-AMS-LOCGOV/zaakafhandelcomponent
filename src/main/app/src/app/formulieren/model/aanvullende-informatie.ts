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
import * as moment from 'moment/moment';
import {PlanItem} from '../../plan-items/model/plan-item';
import {HeadingFormField} from '../../shared/material-form-builder/form-components/heading/heading-form-field';
import {FormulierModus} from './formulier-modus';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';

export class AanvullendeInformatie extends AbstractFormulier {

    formFields: Array<string>;
    formulier: Array<AbstractFormField[]>;

    constructor(modus: FormulierModus, planItem: PlanItem, dataElementen?: Map<string, string>) {
        super(modus, planItem, dataElementen);
    }

    initStartForm() {
        this.formFields = [AIFields.TOELICHTING];

        this.formulier = [
            [new HeadingFormField('doPlanItem', 'actie.taak.aanmaken', '1')],
            [new TextareaFormField(AIFields.TOELICHTING, AIFields.TOELICHTING, this.dataElementen?.get(AIFields.TOELICHTING),
                new FormFieldConfig([Validators.required]))],
            ...this.getGroepAssignment()
        ];
    }

    initBehandelForm() {
        this.formFields = [AIFields.OPGEVRAAGDEINFO, AIFields.DATUMGEVRAAGD, AIFields.DATUMGELEVERD];

        this.formulier = [
            [new ReadonlyFormField(AIFields.TOELICHTING, AIFields.TOELICHTING, this.dataElementen?.get(AIFields.TOELICHTING))],
            [new TextareaFormField(AIFields.OPGEVRAAGDEINFO, AIFields.OPGEVRAAGDEINFO, this.dataElementen?.get(AIFields.OPGEVRAAGDEINFO),
                new FormFieldConfig([Validators.required]))],
            [new DateFormField(AIFields.DATUMGEVRAAGD, AIFields.DATUMGEVRAAGD, moment()),
                new DateFormField(AIFields.DATUMGELEVERD, AIFields.DATUMGELEVERD, moment())]
        ];
    }

}

enum AIFields {
    TOELICHTING = 'toelichting',
    OPGEVRAAGDEINFO = 'opgevraagdeInfo',
    DATUMGEVRAAGD = 'datumGevraagd',
    DATUMGELEVERD = 'datumGeleverd'
}
