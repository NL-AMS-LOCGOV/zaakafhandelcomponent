/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormControlField} from '../../model/abstract-form-control-field';

export class CheckboxFormField extends AbstractFormControlField {
    fieldType = FieldType.CHECKBOX;

    constructor() {
        super();
    }
}
