/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormFieldBuilder} from '../../model/abstract-choices-form-field-builder';
import {RadioFormField} from './radio-form-field';
import {TranslateService} from '@ngx-translate/core';

export class RadioFormFieldBuilder extends AbstractChoicesFormFieldBuilder {

    protected readonly formField: RadioFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new RadioFormField(translate);
    }
}
