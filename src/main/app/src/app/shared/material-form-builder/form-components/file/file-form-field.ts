/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {FileFieldConfig} from '../../model/file-field-config';
import {TranslateService} from '@ngx-translate/core';

export class FileFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.FILE;
    uploadError: string;

    constructor(translate: TranslateService) {
        super(translate);
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
