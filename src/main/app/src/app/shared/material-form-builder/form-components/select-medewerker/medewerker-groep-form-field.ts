/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FieldType} from '../../model/field-type.enum';
import {AbstractFormField} from '../../model/abstract-form-field';
import {Group} from '../../../../identity/model/group';
import {User} from '../../../../identity/model/user';

export class MedewerkerGroepFormField extends AbstractFormField {

    fieldType = FieldType.MEDEWERKER_GROEP;
    defaultGroep: Group;
    defaultMedewerker: User;
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
