/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {FileFormField} from './file-form-field';
import {FileFieldConfig} from '../../model/file-field-config';

export class FileFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: FileFormField;

    constructor() {
        super();
        this.formField = new FileFormField();
    }

    config(config: FileFieldConfig): this {
        this.formField.config = config;
        return this;
    }
}
