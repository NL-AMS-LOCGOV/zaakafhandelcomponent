/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {FileFieldConfig} from '../../model/file-field-config';

export class TaakDocumentUploadFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.TAAK_DOCUMENT_UPLOAD;
    uploadError: string;

    constructor() {
        super();
    }

    getErrorMessage(): string {
        if (this.uploadError) {
            return this.uploadError;
        }
        return super.getErrorMessage();
    }

    get config(): FileFieldConfig {
        return super.config as FileFieldConfig;
    }

    set config(value: FileFieldConfig) {
        super.config = value;
    }

}
