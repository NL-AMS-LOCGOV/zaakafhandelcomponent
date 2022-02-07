/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {HeadingFormField} from './heading-form-field';
import {TranslateService} from '@ngx-translate/core';

export class HeadingFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: HeadingFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new HeadingFormField(translate);
    }

    level(level: string): this {
        this.formField.level = level;
        return this;
    }

    validate(): void {
        if (this.formField.label == null) {
            throw new Error('label is required');
        }
        if (this.formField.level == null) {
            throw new Error('level is required');
        }
    }
}
