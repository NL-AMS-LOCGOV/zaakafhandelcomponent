/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {DocumentenSelecterenFormField} from './documenten-selecteren-form-field';
import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';

export class DocumentenSelecterenFormFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: DocumentenSelecterenFormField;

    constructor() {
        super();
        this.formField = new DocumentenSelecterenFormField();
    }

    documenten(documenten: Observable<EnkelvoudigInformatieobject[]>): this {
        this.formField.documentenObservable = documenten;
        return this;
    }
}
