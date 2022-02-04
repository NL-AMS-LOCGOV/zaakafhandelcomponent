/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from '../../model/abstract-form-field';
import {FieldType} from '../../model/field-type.enum';
import {TranslateService} from '@ngx-translate/core';

export class GoogleMapsFormField extends AbstractFormField {
    fieldType: FieldType = FieldType.GOOGLEMAPS;

    constructor(translate: TranslateService) {
        super(translate);
    }

}
