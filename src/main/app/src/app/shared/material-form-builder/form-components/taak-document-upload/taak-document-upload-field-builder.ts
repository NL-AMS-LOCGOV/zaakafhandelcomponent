/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TaakDocumentUploadFormField} from './taak-document-upload-form-field';
import {AbstractFileFormFieldBuilder} from '../../model/abstract-file-form-field-builder';
import {TranslateService} from '@ngx-translate/core';

export class TaakDocumentUploadFieldBuilder extends AbstractFileFormFieldBuilder {
    protected readonly formField: TaakDocumentUploadFormField;

    constructor(translate: TranslateService) {
        super();
        this.formField = new TaakDocumentUploadFormField(translate);
        this.updateHint();
    }

    defaultTitel(titel: string): this {
        this.formField.defaultTitel = titel;
        return this;
    }

    zaakUUID(zaakUUID: string): this {
        this.formField.zaakUUID = zaakUUID;
        return this;
    }

    validate(): void {
        super.validate();
        if (!this.formField.zaakUUID) {
            throw new Error('zaakUUID is required');
        }
    }

}
