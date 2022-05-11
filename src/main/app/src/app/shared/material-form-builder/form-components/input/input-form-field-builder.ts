/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {InputFormField} from './input-form-field';
import {ActionIcon} from '../../../edit/action-icon';

export class InputFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: InputFormField;

    constructor() {
        super();
        this.formField = new InputFormField();
    }

    icon(icon: ActionIcon): this {
        this.formField.icons = [icon];
        return this;
    }

    icons(icons: ActionIcon[]): this {
        this.formField.icons = icons;
        return this;
    }

    maxlength(maxlength: number, showCount?: boolean): this {
        this.formField.maxlength = maxlength;
        this.formField.showCount = showCount;
        return this;
    }

    build() {
        super.validate();
        return this.formField;
    }

}
