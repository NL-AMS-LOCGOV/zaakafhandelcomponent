/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TranslateService} from '@ngx-translate/core';
import {ParagraphFormField} from './paragraph-form-field';

export class ParagraphFormFieldBuilder {

    protected readonly formField: ParagraphFormField;

    constructor(translate: TranslateService) {
        this.formField = new ParagraphFormField(translate);
    }

    validate(): void {
        if (this.formField.label == null) {
            throw new Error('label is required');
        }
    }

    text(text: string): this {
        this.formField.label = text;
        return this;
    }

    build() {
        this.validate();
        return this.formField;
    }
}
