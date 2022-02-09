/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from './abstract-form-field';
import {AppGlobals} from '../../../app.globals';

export abstract class AbstractFileFormField extends AbstractFormField {

    fileTypes: string = AppGlobals.ALLOWED_FILETYPES;
    fileSizeMB: number = AppGlobals.FILE_MAX_SIZE;
    uploadURL: string;
    uploadError: string;

    protected constructor() {
        super();
    }

}
