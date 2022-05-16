/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';

export class DividerFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.DIVIDER;

    constructor() {
        super();
    }
}
