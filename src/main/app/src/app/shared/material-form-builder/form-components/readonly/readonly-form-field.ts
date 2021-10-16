/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';

export class ReadonlyFormField extends AbstractFormField {
    fieldType = FieldType.READONLY;

    constructor(id: string, label: string, value: any) {
        super(id, label, value);
    }
}
