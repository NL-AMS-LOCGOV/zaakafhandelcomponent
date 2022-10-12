/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class ReadonlyFormField extends AbstractFormControlFormField {
    fieldType = FieldType.READONLY;

    constructor() {
        super();
    }
}
