/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from './abstract-form-field-builder';
import {AbstractFileFormField} from './abstract-file-form-field';
import {FormFieldHint} from './form-field-hint';

export abstract class AbstractFileFormFieldBuilder extends AbstractFormFieldBuilder {

    protected abstract readonly formField: AbstractFileFormField;

    constructor() {
        super();
    }

    maxFileSizeMB(mb: number): this {
        this.formField.fileSizeMB = mb;
        this.updateHint();
        return this;
    }

    allowedFileTypes(fileTypes: string): this {
        this.formField.fileTypes = fileTypes;
        this.updateHint();
        return this;
    }

    uploadURL(url: string): this {
        this.formField.uploadURL = url;
        return this;
    }

    validate(): void {
        super.validate();
        if (!this.formField.uploadURL) {
            throw new Error('Missing value for restURL');
        }
    }

    protected updateHint() {
        this.formField.hint = new FormFieldHint(
            'Maximale bestandsgrootte: ' + this.formField.fileSizeMB + 'MB | toegestane bestandstypen: ' + this.formField.fileTypes, 'end');
    }

}
