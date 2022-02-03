/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormFieldBuilder} from '../../model/abstract-choices-form-field-builder';
import {SelectFormField} from './select-form-field';
import {TranslateService} from '@ngx-translate/core';

export class SelectFormFieldBuilder extends AbstractChoicesFormFieldBuilder {

    protected readonly formField: SelectFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new SelectFormField(translate);
    }
}
