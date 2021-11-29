/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from './abstract-form-field-builder';
import {AbstractChoicesFormField} from './abstract-choices-form-field';
import {Observable} from 'rxjs';

export abstract class AbstractChoicesFormFieldBuilder extends AbstractFormFieldBuilder {

    protected abstract readonly formField: AbstractChoicesFormField;

    constructor() {
        super();
    }

    optionLabel(optionLabel: string): this {
        this.formField.optionLabel = optionLabel;
        return this;
    }

    options(options: Observable<any[]>): this {
        this.formField.options = options;
        return this;
    }

}
