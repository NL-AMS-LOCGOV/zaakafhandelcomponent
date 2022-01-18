/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Observable} from 'rxjs';

export class ConfirmDialogData {
    message: string;
    messageParams: {};
    observable: Observable<any>;

    constructor(message: string, messageParams?: {}, observable?: Observable<any>) {
        this.message = message;
        this.messageParams = messageParams;
        this.observable = observable;
    }
}
