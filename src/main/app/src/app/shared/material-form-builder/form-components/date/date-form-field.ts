/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class DateFormField extends AbstractFormControlFormField {

    fieldType = FieldType.DATE;
    public minDate: Date;
    public maxDate: Date;

    constructor() {
        super();
    }
}
