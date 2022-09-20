/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {TextareaFormField} from './textarea-form-field';
import {FormControl} from '@angular/forms';

export class TextareaFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: TextareaFormField;

    constructor(value?:any) {
        super();
        this.formField = new TextareaFormField();
        this.formField.initFormControl(value);
    }

    maxlength(maxlength: number): this {
        this.formField.maxlength = maxlength;
        return this;
    }

}
