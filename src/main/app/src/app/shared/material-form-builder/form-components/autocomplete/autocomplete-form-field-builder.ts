/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormFieldBuilder} from '../../model/abstract-choices-form-field-builder';
import {AutocompleteFormField} from './autocomplete-form-field';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';

export class AutocompleteFormFieldBuilder extends AbstractChoicesFormFieldBuilder {

    protected readonly formField: AutocompleteFormField;

    constructor(value?: any | Observable<any>) {
        super();
        this.formField = new AutocompleteFormField();
        this.formField.initFormControl(value);
    }

    maxlength(maxlength: number): this {
        this.formField.maxlength = maxlength;
        return this;
    }

}
