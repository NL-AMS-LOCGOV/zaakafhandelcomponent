/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';

import {Groep} from '../../../../identity/model/groep';
import {Medewerker} from '../../../../identity/model/medewerker';
import {MedewerkerGroepFormField} from './medewerker-groep-form-field';

export class MedewerkerGroepFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: MedewerkerGroepFormField;

    constructor() {
        super();
        this.formField = new MedewerkerGroepFormField();
    }

    groepOptioneel(): this {
        this.formField.groepOptioneel = true;
        return this;
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
