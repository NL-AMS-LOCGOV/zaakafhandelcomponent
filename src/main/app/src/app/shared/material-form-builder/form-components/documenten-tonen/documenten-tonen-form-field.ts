/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';

export class DocumentenTonenFormField extends AbstractFormField {
    fieldType = FieldType.DOCUMENTEN_TONEN;

    constructor() {
        super();
    }
}
