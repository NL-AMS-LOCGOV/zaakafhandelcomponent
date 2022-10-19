/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AppGlobals} from '../../../app.globals';
import {Observable, Subject} from 'rxjs';
import {AbstractFormControlField} from './abstract-form-control-field';

export abstract class AbstractFileFormField extends AbstractFormControlField {

    fileTypes: string = AppGlobals.ALLOWED_FILETYPES;
    fileSizeMB: number = AppGlobals.FILE_MAX_SIZE;
    uploadURL: string;
    uploadError: string;
    fileUploaded$ = new Subject<string>();
    reset$ = new Subject<void>();

    protected constructor() {
        super();
    }

    get fileUploaded(): Observable<string> {
        return this.fileUploaded$.asObservable();
    }

    reset() {
        this.reset$.next();
    }
}
