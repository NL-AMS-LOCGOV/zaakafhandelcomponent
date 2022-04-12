/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Observable} from 'rxjs';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';

export class DialogData {

    public confirmButtonActionKey: string = 'actie.ja';
    public cancelButtonActionKey: string = 'actie.annuleren';

    constructor(public formField: AbstractFormField,
                public fn?: (result: any) => Observable<any>,
                public melding?: string,
                public uitleg?: string) {}
}
