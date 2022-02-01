/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';
import {TranslateService} from '@ngx-translate/core';

export class CheckboxFormField extends AbstractFormField {
    fieldType = FieldType.CHECKBOX;

    constructor(translate: TranslateService) {
        super(translate);
    }
}
