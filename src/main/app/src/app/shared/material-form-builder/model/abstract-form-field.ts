/*
 * SPDX-FileCopyrightText: 2021 Atos
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

    getErrorMessage(): string {
        if (this.formControl.hasError('required')) {
            return 'verplicht';
        } else if (this.formControl.hasError('min')) {
            return `min, min: ${this.formControl.errors.min.min}, actual: ${this.formControl.errors.min.actual}`;
        } else if (this.formControl.hasError('max')) {
            return `max, max: ${this.formControl.errors.max.min}, actual: ${this.formControl.errors.max.actual}`;
        } else if (this.formControl.hasError('minlength')) {
            return `minlength, requiredLength: ${this.formControl.errors.minlength.requiredLength}, actualLength: ${this.formControl.errors.minlength.actualLength}`;
        } else if (this.formControl.hasError('maxlength')) {
            return `maxlength, requiredLength: ${this.formControl.errors.maxlength.requiredLength}, actualLength: ${this.formControl.errors.maxlength.actualLength}`;
        } else if (this.formControl.hasError('email')) {
            return 'email';
        } else if (this.formControl.hasError('pattern')) {
            return `pattern, requiredPattern: ${this.formControl.errors.pattern.requiredPattern}, actualValue: ${this.formControl.errors.pattern.actualValue}`;
        } else {
            return '';
        }
    }

    hasReadonlyView() {
        return false;
    }
}
