/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {InputFormField} from './input-form-field';
import {TranslateService} from '@ngx-translate/core';

export class InputFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: InputFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new InputFormField(translate);
    }

}
