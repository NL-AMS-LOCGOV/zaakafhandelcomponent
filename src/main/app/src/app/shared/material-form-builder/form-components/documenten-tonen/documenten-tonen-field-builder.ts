/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {DocumentenTonenFormField} from './documenten-tonen-form-field';

export class DocumentenTonenFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: DocumentenTonenFormField;

    constructor() {
        super();
        this.formField = new DocumentenTonenFormField();
    }

}
