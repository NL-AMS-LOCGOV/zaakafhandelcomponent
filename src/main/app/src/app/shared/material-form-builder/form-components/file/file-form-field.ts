/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {FileFieldConfig} from '../../model/file-field-config';

export class FileFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.FILE;
    uploadError: string;

    constructor(id: string, label: string, value: any, config?: FileFieldConfig) {
        super(id, label, null, config);
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
}
