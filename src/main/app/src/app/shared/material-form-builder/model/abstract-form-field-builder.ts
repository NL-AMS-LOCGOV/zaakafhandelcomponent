/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from './abstract-form-field';
import {ValidatorFn, Validators} from '@angular/forms';
import {FormFieldHint} from './form-field-config';

export abstract class AbstractFormFieldBuilder {

    protected abstract readonly formField: AbstractFormField;

    protected constructor() {
    }

    id(id: string): this {
        this.formField.id = id;
        return this;
    }

    label(label: string): this {
        this.formField.label = label;
        return this;
    }

    readonly(readonly: boolean): this {
        this.formField.readonly = readonly;
        return this;
    }

    value(value: any): this {
        this.formField.formControl.setValue(value);
        return this;
    }

    validators(...validators: ValidatorFn[]): this {
        this.formField.formControl.setValidators(validators);
        if (validators.find((v) => v === Validators.required) ||
            validators.find((v) => v === Validators.requiredTrue)) {
            this.formField.required = true;
        }
        return this;
    }

    hint(hint: FormFieldHint): this {
        this.formField.hint = hint;
        return this;
    }

    build() {
        this.validate();
        return this.formField;
    }

    validate() {
        if (!this.formField.id) {
            throw new Error('Missing implementation of id');
        }
        if (!this.formField.label) {
            throw new Error('Missing implementation of label');
        }
    }
}
