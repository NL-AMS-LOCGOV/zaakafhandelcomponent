/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {HeadingFormField} from './heading-form-field';

export class HeadingFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: HeadingFormField;

    constructor() {
        super();
        this.formField = new HeadingFormField();
    }

    level(level: string): AbstractFormFieldBuilder {
        this.formField.level = level;
        return this;
    }
}
