/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {ActionIcon} from '../../../edit/action-icon';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class InputFormField extends AbstractFormControlFormField {
    fieldType: FieldType = FieldType.INPUT;
    icons: ActionIcon[];
    maxlength: number;
    showCount: boolean = false;

    constructor() {
        super();
    }

}
