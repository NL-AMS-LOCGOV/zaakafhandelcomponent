/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Observable} from 'rxjs';

export class FormConfig {

    private readonly _saveButtonText$: Observable<string>;
    private readonly _cancelButtonText$: Observable<string>;

    constructor(saveButtonText: Observable<string>, cancelButtonText: Observable<string>) {
        this._saveButtonText$ = saveButtonText;
        this._cancelButtonText$ = cancelButtonText;
    }

    get saveButtonText(): Observable<string> {
        return this._saveButtonText$;
    }

    get cancelButtonText(): Observable<string> {
        return this._cancelButtonText$;
    }

}
