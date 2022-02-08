/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';

export class ParagraphFormField extends AbstractFormField {
    fieldType = FieldType.PARAGRAPH;

    constructor() {
        super();
    }
}
