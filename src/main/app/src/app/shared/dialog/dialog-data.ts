/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Observable} from 'rxjs';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';

export class DialogData {

    public confirmButtonActionKey: string = 'actie.ja';
    public cancelButtonActionKey: string = 'actie.annuleren';

    constructor(public formFields: AbstractFormField[],
                public fn?: (results: any[]) => Observable<any>,
                public melding?: string,
                public uitleg?: string) {}

    formFieldsInvalid(): boolean {
        for (const formField of this.formFields) {
            if (formField.formControl.invalid) {
                return true;
            }
        }
        return false;
    }
}
