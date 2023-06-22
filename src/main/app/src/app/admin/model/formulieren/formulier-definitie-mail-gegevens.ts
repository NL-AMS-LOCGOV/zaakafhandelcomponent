/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormControl, FormGroup} from '@angular/forms';

export class FormulierDefinitieMailGegevens {
    from: string;
    to: string;
    subject: string;
    body: string;
    cc: string;
    bcc: string;

    static asFormGroup(vd: FormulierDefinitieMailGegevens): FormGroup {
        return new FormGroup({
            from: new FormControl(vd.from),
            to: new FormControl(vd.to),
            subject: new FormControl(vd.subject),
            body: new FormControl(vd.body),
            cc: new FormControl(vd.cc),
            bcc: new FormControl(vd.bcc),
        });
    }
}
