/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';
import {Groep} from '../../../../identity/model/groep';
import {Medewerker} from '../../../../identity/model/medewerker';

export class MedewerkerGroepFormField extends AbstractFormField {

    fieldType = FieldType.MEDEWERKER_GROEP;
    defaultGroep: Groep;
    defaultMedewerker: Medewerker;
    groepOptioneel = false;

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
