/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';

export class HeadingFormField extends AbstractFormField {
    fieldType = FieldType.HEADING;

    constructor(id: string, label: string, public value: any) {
        super(id, label, value);
    }
}
