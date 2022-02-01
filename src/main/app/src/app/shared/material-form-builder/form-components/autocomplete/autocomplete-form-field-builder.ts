/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormFieldBuilder} from '../../model/abstract-choices-form-field-builder';
import {AutocompleteFormField} from './autocomplete-form-field';
import {Observable} from 'rxjs';
import {AutocompleteValidators} from './autocomplete-validators';
import {TranslateService} from '@ngx-translate/core';

export class AutocompleteFormFieldBuilder extends AbstractChoicesFormFieldBuilder {

    protected readonly formField: AutocompleteFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new AutocompleteFormField(translate);
    }

    options(options: Observable<any[]>): this {
        this.formField.formControl.addAsyncValidators(AutocompleteValidators.asyncOptionInList(options));
        return super.options(options);
    }
}
