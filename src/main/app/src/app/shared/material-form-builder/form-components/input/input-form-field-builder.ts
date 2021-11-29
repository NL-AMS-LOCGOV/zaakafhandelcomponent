/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {InputFormField} from './input-form-field';

export class InputFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: InputFormField;

    constructor() {
        super();
        this.formField = new InputFormField();
    }

}
