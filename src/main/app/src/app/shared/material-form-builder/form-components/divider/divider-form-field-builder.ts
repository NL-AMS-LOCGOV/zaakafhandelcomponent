/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {DividerFormField} from './divider-form-field';

export class DividerFormFieldBuilder extends AbstractFormFieldBuilder {
    protected readonly formField: DividerFormField;

    constructor() {
        super();
        this.formField = new DividerFormField();
    }

    build() {
        return this.formField;
    }
}
