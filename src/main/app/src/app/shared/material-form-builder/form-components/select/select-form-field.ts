/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {FormFieldConfig} from '../../model/form-field-config';
import {AbstractChoicesFormField} from '../../model/abstract-choices-form-field';

export class SelectFormField extends AbstractChoicesFormField {
    fieldType = FieldType.SELECT;

    constructor(id: string, label: string, value: any, optionLabel: string | null, options: any[], config?: FormFieldConfig) {
        super(id, label, value, optionLabel, options, config);
    }

}
