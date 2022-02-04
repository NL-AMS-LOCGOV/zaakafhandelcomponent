/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFileFormField} from '../../model/abstract-file-form-field';
import {TranslateService} from '@ngx-translate/core';

export class TaakDocumentUploadFormField extends AbstractFileFormField {
    fieldType: FieldType = FieldType.TAAK_DOCUMENT_UPLOAD;

    defaultTitel: string;
    zaakUUID: string;

    constructor(translate: TranslateService) {
        super(translate);
    }

}
