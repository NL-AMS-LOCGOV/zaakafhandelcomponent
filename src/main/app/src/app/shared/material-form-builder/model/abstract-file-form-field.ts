/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from './abstract-form-field';
import {AppGlobals} from '../../../app.globals';
import {Observable, Subject} from 'rxjs';
import {AbstractFormControlFormField} from './abstract-form-control-form-field';

export abstract class AbstractFileFormField extends AbstractFormControlFormField {

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
