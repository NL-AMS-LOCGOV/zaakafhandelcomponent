/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';

export class DocumentenSelecterenFormField extends AbstractFormField {

    documentenObservable: Observable<EnkelvoudigInformatieobject[]>;
    fieldType = FieldType.DOCUMENTEN_SELECTEREN;

    constructor() {
        super();

    }
}
