/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {ActionIcon} from '../../../edit/action-icon';
import {AbstractFormControlField} from '../../model/abstract-form-control-field';

export class HtmlEditorFormField extends AbstractFormControlField {
    fieldType: FieldType = FieldType.HTML_EDITOR;
    icons: ActionIcon[];
    maxlength: number;
    showCount: boolean = false;

    constructor() {
        super();
    }
}
