/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {DateFormField} from './date-form-field';
import {Observable} from 'rxjs';

export class DateFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: DateFormField;

    constructor(value?: any) {
        super();
        this.formField = new DateFormField();
        this.formField.initFormControl(value);
    }

    minDate(date: Date) {
        this.formField.minDate = date;
        return this;
    }

    maxDate(date: Date) {
        this.formField.maxDate = date;
        return this;
    }

}
