/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormFieldBuilder} from '../../model/abstract-choices-form-field-builder';
import {SelectFormField} from './select-form-field';

export class SelectFormFieldBuilder extends AbstractChoicesFormFieldBuilder {

    protected readonly formField: SelectFormField;

    constructor() {
        super();
        this.formField = new SelectFormField();
    }
}
