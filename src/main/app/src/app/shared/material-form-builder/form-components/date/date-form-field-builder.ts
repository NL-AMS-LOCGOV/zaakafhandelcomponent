/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {DateFormField} from './date-form-field';
import {TranslateService} from '@ngx-translate/core';

export class DateFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: DateFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new DateFormField(translate);
    }
}
