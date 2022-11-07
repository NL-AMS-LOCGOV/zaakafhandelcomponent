/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {ActionIcon} from '../../../edit/action-icon';
import {HtmlEditorFormField} from './html-editor-form-field';

export class HtmlEditorFormFieldBuilder extends AbstractFormFieldBuilder {

    readonly formField: HtmlEditorFormField;

    constructor(value?: any) {
        super();
        this.formField = new HtmlEditorFormField();
        this.formField.initControl(value);
    }

    icon(icon: ActionIcon): this {
        this.formField.icons = [icon];
        return this;
    }

    icons(icons: ActionIcon[]): this {
        this.formField.icons = icons;
        return this;
    }

    maxlength(maxlength: number, showCount: boolean = true): this {
        this.formField.maxlength = maxlength;
        this.formField.showCount = showCount;
        return this;
    }
}
