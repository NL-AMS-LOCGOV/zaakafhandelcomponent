/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractChoicesFormField} from '../../../model/abstract-choices-form-field';
import {FieldType} from '../../../model/field-type.enum';
import {FormFieldConfig} from '../../../model/form-field-config';
import {Groep} from '../../../../../identity/model/groep';

export class SelectGroepFormField extends AbstractChoicesFormField {
    fieldType = FieldType.SELECT_GROEP;

    constructor(value: Groep, config?: FormFieldConfig) {
        super('groep', 'groep.-kies-', value, 'naam', [], config);
    }

}
