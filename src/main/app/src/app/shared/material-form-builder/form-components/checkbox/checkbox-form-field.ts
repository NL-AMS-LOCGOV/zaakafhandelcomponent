/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class CheckboxFormField extends AbstractFormControlFormField {
    fieldType = FieldType.CHECKBOX;

    constructor() {
        super();
    }
}
