/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FileFormField} from './file-form-field';
import {AbstractFileFormFieldBuilder} from '../../model/abstract-file-form-field-builder';
import {TranslateService} from '@ngx-translate/core';

export class FileFormFieldBuilder extends AbstractFileFormFieldBuilder {
    protected readonly formField: FileFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new FileFormField(translate);
        this.updateHint();
    }

}
