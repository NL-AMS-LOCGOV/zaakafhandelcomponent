/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {ActionIcon} from '../../../edit/action-icon';

export class InputFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.INPUT;
    icons: ActionIcon[];

    constructor() {
        super();
    }


}
