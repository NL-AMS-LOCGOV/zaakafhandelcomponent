/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormFieldBuilder} from '../../model/abstract-choices-form-field-builder';
import {RadioFormField} from './radio-form-field';

export class RadioFormFieldBuilder extends AbstractChoicesFormFieldBuilder {

    protected readonly formField: RadioFormField;

    constructor() {
        super();
        this.formField = new RadioFormField();
    }
}
