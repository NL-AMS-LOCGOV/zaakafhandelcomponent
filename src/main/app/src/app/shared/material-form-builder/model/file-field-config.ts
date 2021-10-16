/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ValidatorFn} from '@angular/forms';
import {FormFieldConfig, FormFieldHint} from './form-field-config';

export class FileFieldConfig extends FormFieldConfig {

    private _fileTypes: string = '.pdf,.doc,.docx,.xls,.xlsx,.pptx,.pptx,.vsd,.png,.gif,.jpg,.jpeg,.jpeg,.txt,.bmp,.odt,.rtf';
    private _fileSizeMB: number = 20;
    private _restURL: string;

    constructor(restURL: string, validators?: ValidatorFn[], maxFileSizeMB?: number, fileTypes?: string, hint?: FormFieldHint) {
        super(validators, hint);
        this._restURL = restURL;
        if (fileTypes) {
            this._fileTypes = fileTypes;
        }
        if (maxFileSizeMB) {
            this._fileSizeMB = maxFileSizeMB;
        }
        if (!hint) {
            super.hint = new FormFieldHint('Maximale bestandsgrootte: ' + this._fileSizeMB + 'MB | toegestane bestandstypen: ' + this._fileTypes, 'end');
        }
    }

    get fileTypes(): string {
        return this._fileTypes;
    }

    get fileSizeMB(): number {
        return this._fileSizeMB;
    }

    get restURL(): string {
        return this._restURL;
    }
}
