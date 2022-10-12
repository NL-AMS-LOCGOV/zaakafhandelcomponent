/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractControl, FormControl, FormControlOptions, FormGroup} from '@angular/forms';
import {FieldType} from './field-type.enum';
import {FormFieldHint} from './form-field-hint';
import {AbstractFormField} from './abstract-form-field';

export abstract class AbstractFormGroupFormField extends AbstractFormField {

    formControl: FormGroup;

    protected constructor() {
        super();
    }

    initControl(value: {[key: string]: FormControl<any>}): void {
        this.formControl = new FormGroup(value);
    }
}
