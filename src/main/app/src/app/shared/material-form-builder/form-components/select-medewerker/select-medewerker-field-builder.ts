/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SelectMedewerkerFormField} from './select-medewerker-form-field';
import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';

import {Groep} from '../../../../identity/model/groep';
import {Medewerker} from '../../../../identity/model/medewerker';

export class SelectMedewerkerFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: SelectMedewerkerFormField;

    constructor() {
        super();
        this.formField = new SelectMedewerkerFormField();
    }

    defaultGroep(groep: Groep): this {
        this.formField.defaultGroep = groep;
        return this;
    }

    defaultMedewerker(medewerker: Medewerker): this {
        this.formField.defaultMedewerker = medewerker;
        return this;
    }

    build() {
        return this.formField;
    }

}
