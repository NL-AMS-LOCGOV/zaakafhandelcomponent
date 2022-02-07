/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractChoicesFormField} from '../../model/abstract-choices-form-field';
import {TranslateService} from '@ngx-translate/core';

export class RadioFormField extends AbstractChoicesFormField {
    fieldType = FieldType.RADIO;

    constructor(translate: TranslateService) {
        super(translate);
    }
}
