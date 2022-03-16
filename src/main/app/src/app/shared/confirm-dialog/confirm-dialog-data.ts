/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Observable} from 'rxjs';
import {AbstractFormField} from '../material-form-builder/model/abstract-form-field';

export class ConfirmDialogData {

    confirmButtonActionKey: string;
    cancelButtonActionKey: string;
    formField: AbstractFormField;
    fn: (result: any) => Observable<any>;

    constructor(confirmButtonActionKey: string, cancelButtonActionKey: string, formField: AbstractFormField, fn?: (result: any) => Observable<any>) {
        this.confirmButtonActionKey = confirmButtonActionKey;
        this.cancelButtonActionKey = cancelButtonActionKey;
        this.formField = formField;
        this.fn = fn;
    }
}
