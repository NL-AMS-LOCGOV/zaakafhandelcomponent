/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {FileFormField} from './file-form-field';
import {FileFieldConfig} from '../../model/file-field-config';
import {TranslateService} from '@ngx-translate/core';

export class FileFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: FileFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new FileFormField(translate);
    }

    config(config: FileFieldConfig): this {
        this.formField.config = config;
        return this;
    }
}
