/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {DocumentSelectFormField} from '../document-select/document-select-form-field';

export class DocumentOndertekenenFormField extends DocumentSelectFormField {

    fieldType = FieldType.DOCUMENT_ONDERTEKENEN;

    constructor() {
        super();
    }

}
