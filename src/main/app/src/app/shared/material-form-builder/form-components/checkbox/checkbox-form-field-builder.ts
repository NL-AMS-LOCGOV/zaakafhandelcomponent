/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {CheckboxFormField} from './checkbox-form-field';
import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';

export class CheckboxFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: CheckboxFormField;

    constructor() {
        super();
        this.formField = new CheckboxFormField();
    }
}
