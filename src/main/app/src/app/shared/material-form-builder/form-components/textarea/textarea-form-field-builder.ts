/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {TextareaFormField} from './textarea-form-field';

export class TextareaFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: TextareaFormField;

    constructor() {
        super();
        this.formField = new TextareaFormField();
    }

}
