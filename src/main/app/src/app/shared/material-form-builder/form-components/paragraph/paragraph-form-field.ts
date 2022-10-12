/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class ParagraphFormField extends AbstractFormControlFormField {
    fieldType = FieldType.PARAGRAPH;

    constructor() {
        super();
    }
}
