/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {DocumentSelectFieldBuilder} from '../document-select/document-select-field-builder';
import {DocumentOndertekenenFormField} from './document-ondertekenen-form-field';

export class DocumentOndertekenenFieldBuilder extends DocumentSelectFieldBuilder {

    readonly formField: DocumentOndertekenenFormField;

    constructor() {
        super();
        this.formField = new DocumentOndertekenenFormField();
        this.formField.selectLabel = 'ondertekenen';
        this.formField.initControl();
    }
}
