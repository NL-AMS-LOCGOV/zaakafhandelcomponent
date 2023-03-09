/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {DocumentSelectFormField} from './document-select-form-field';

export class DocumentSelectFieldBuilder extends AbstractFormFieldBuilder {

    readonly formField: DocumentSelectFormField;

    constructor() {
        super();
        this.formField = new DocumentSelectFormField();
        this.formField.initControl();
    }

    documenten(documenten: Observable<EnkelvoudigInformatieobject[]>): this {
        this.formField.documenten = documenten;
        return this;
    }

    documentenChecked(documentUUIDs: string[]): this {
        this.formField.documentenChecked = documentUUIDs;
        return this;
    }

    removeColumn(column: 'select' | 'titel' | 'documentType' | 'status' | 'versie' | 'auteur' | 'creatiedatum' | 'bestandsomvang' | 'url'): this {
        this.formField.removeColumn(column);
        return this;
    }

    selectLabel(label: string): this {
        this.formField.selectLabel = label;
        return this;
    }
}
