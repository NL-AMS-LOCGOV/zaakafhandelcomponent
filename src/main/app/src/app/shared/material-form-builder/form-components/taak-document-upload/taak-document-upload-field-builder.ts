/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {TaakDocumentUploadFormField} from './taak-document-upload-form-field';
import {FileFieldConfig} from '../../model/file-field-config';

export class TaakDocumentUploadFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: TaakDocumentUploadFormField;

    constructor() {
        super();
        this.formField = new TaakDocumentUploadFormField();
    }

    config(config: FileFieldConfig): this {
        this.formField.config = config;
        return this;
    }
}
