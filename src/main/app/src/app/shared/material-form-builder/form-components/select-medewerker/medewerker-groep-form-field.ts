/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';

export class MedewerkerGroepFormField extends AbstractFormField {

    fieldType = FieldType.MEDEWERKER_GROEP;
    defaultGroepId: string;
    groepLabel: string;
    defaultMedewerkerId: string;
    medewerkerLabel: string;
    groepOptioneel = false;
    maxlength: number;

    constructor() {
        super();
    }

    /**
     * implements own readonly view, dont use the default read-only-component
     */
    hasReadonlyView() {
        return true;
    }

}
