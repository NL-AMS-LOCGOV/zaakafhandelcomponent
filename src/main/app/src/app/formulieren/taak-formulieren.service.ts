/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {FormGroup} from '@angular/forms';

@Injectable({
    providedIn: 'root'
})
export class TaakFormulierenService {

    constructor() { }

    public getDataElementen(formGroup: FormGroup): Map<string, string> {
        let dataElementen: Map<string, string> = new Map<string, string>();

        Object.keys(formGroup.controls).forEach((key) => {
            dataElementen.set(key, formGroup.controls[key].value);
        });

        return dataElementen;
    }

}
