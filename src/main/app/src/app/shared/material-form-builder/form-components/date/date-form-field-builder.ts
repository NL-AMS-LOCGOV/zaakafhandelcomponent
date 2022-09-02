/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {DateFormField} from './date-form-field';

export class DateFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: DateFormField;

    constructor() {
        super();
        this.formField = new DateFormField();
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
