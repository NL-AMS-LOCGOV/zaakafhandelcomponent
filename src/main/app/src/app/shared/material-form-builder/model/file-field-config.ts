/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ValidatorFn} from '@angular/forms';
import {FormFieldConfig, FormFieldHint} from './form-field-config';
import {AppGlobals} from '../../../app.globals';

export class FileFieldConfig extends FormFieldConfig {

    public fileTypes: string = '.pdf,.doc,.docx,.xls,.xlsx,.pptx,.pptx,.vsd,.png,.gif,.jpg,.jpeg,.jpeg,.txt,.bmp,.odt,.rtf';
    public fileSizeMB: number = AppGlobals.FILE_MAX_SIZE;
    public restURL: string;
    public zaakUUID: string;

    constructor(restURL: string, validators?: ValidatorFn[], maxFileSizeMB?: number, fileTypes?: string, hint?: FormFieldHint) {
        super(validators, hint);
        this.restURL = restURL;
        if (fileTypes) {
            this.fileTypes = fileTypes;
        }
        if (maxFileSizeMB) {
            this.fileSizeMB = maxFileSizeMB;
        }
        if (!hint) {
            super.hint = new FormFieldHint('Maximale bestandsgrootte: ' + this.fileSizeMB + 'MB | toegestane bestandstypen: ' + this.fileTypes, 'end');
        }
    }
}
