/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class TextareaFormField extends AbstractFormControlFormField {
    fieldType = FieldType.TEXTAREA;
    maxlength: number;
    showCount: boolean = false;

    constructor() {
        super();
    }
}
