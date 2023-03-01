/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {ActionIcon} from '../../../edit/action-icon';
import {AbstractFormControlField} from '../../model/abstract-form-control-field';
import {Subject} from 'rxjs';

export class InputFormField extends AbstractFormControlField {
    fieldType: FieldType = FieldType.INPUT;
    icons: ActionIcon[];
    clicked: Subject<any> = new Subject<any>();
    maxlength: number;
    showCount: boolean = false;

    constructor() {
        super();
    }

}
