/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {EventEmitter} from '@angular/core';
import {AbstractFormControlFormField} from '../../model/abstract-form-control-form-field';

export class DocumentenLijstFormField extends AbstractFormControlFormField {

    fieldType = FieldType.DOCUMENTEN_LIJST;
    documenten$: Observable<EnkelvoudigInformatieobject[]>;
    documentenChanged = new EventEmitter<Observable<EnkelvoudigInformatieobject[]>>();
    documentenCheckedVoorOndertekenen: string[];
    documentenChecked: string[];

    ondertekenen: boolean;
    verbergStatus: boolean;

    constructor() {
        super();
    }

    /**
     * implements own readonly view, dont use the default read-only-component
     */
    hasReadonlyView() {
        return true;
    }

    setDocumenten$(documenten: Observable<EnkelvoudigInformatieobject[]>) {
        this.documentenChanged.emit(documenten);
    }

}
