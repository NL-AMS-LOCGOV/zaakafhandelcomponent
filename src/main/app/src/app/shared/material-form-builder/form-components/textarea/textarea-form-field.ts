/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {FormFieldConfig} from '../../model/form-field-config';

export class TextareaFormField extends AbstractFormField {
    fieldType = FieldType.TEXTAREA;

    constructor(id: string, label: string, value: any, config?: FormFieldConfig) {
        super(id, label, value, config);
    }
}
