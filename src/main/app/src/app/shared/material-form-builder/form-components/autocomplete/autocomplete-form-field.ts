/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormField} from '../../model/abstract-choices-form-field';
import {FieldType} from '../../model/field-type.enum';
import {FormFieldConfig} from '../../model/form-field-config';
import {Observable} from 'rxjs';

export class AutocompleteFormField extends AbstractChoicesFormField {
    fieldType = FieldType.AUTOCOMPLETE;

    constructor(id: string, label: string, value: any, optionLabel: string | null, options: Observable<any[]>, config?: FormFieldConfig) {
        super(id, label, value, optionLabel, options, config);
    }
}
