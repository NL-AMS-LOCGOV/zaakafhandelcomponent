/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormControl} from '@angular/forms';
import {FieldType} from './field-type.enum';
import {FormFieldHint} from './form-field-hint';

export abstract class AbstractFormField {
    id: string;
    label: string;
    required: boolean;
    readonly: boolean;
    readonly formControl: FormControl = new FormControl();
    hint: FormFieldHint;

    abstract fieldType: FieldType;

    protected constructor() {
    }

    hasReadonlyView() {
        return false;
    }
}
