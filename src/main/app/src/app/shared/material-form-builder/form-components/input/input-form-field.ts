/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {Observable, Subject} from 'rxjs';

export class InputFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.INPUT;
    iconClicked$ = new Subject<void>();
    icon: string;

    constructor() {
        super();
    }

    get iconClicked(): Observable<void> {
        return this.iconClicked$.asObservable();
    }

}
