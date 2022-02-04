/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {TranslateService} from '@ngx-translate/core';

export class DocumentenLijstFormField extends AbstractFormField {

    fieldType = FieldType.DOCUMENTEN_LIJST;
    documenten$: Observable<EnkelvoudigInformatieobject[]>;

    constructor(translate: TranslateService) {
        super(translate);
    }

    /**
     * implements own readonly view, dont use the default read-only-component
     */
    hasReadonlyView() {
        return true;
    }

}
