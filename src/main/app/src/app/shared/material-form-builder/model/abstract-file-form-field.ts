/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AppGlobals} from '../../../app.globals';
import {Observable, Subject} from 'rxjs';
import {AbstractFormControlField} from './abstract-form-control-field';

export abstract class AbstractFileFormField extends AbstractFormControlField {

    fileTypes: string = AppGlobals.ALLOWED_FILETYPES;
    fileSizeMiB: number = AppGlobals.FILE_MAX_SIZE;
    uploadURL: string;
    uploadError: string;
    fileUploaded$ = new Subject<string>();
    reset$ = new Subject<void>();

    protected constructor() {
        super();
    }

    isBestandstypeToegestaan(file: File): boolean {
        const extensies = this.fileTypes.split(/\s*,\s*/).map(s => s.trim().toLowerCase());
        return extensies.indexOf(this.getBestandsextensie(file)) > -1;
    }

    isBestandsgrootteToegestaan(file: File): boolean {
        return file.size <= this.fileSizeMiB * 1024 * 1024;
    }

    getBestandsextensie(file: File) {
        if (file.name.indexOf('.') < 1) {
            return '-';
        }
        return '.' + file.name.split('.').pop().toLowerCase();
    }

    getBestandsgrootteMiB(file: File): string {
        return parseFloat(String(file.size / 1024 / 1024)).toFixed(2);
    }

    get fileUploaded(): Observable<string> {
        return this.fileUploaded$.asObservable();
    }

    reset() {
        this.reset$.next();
    }
}
