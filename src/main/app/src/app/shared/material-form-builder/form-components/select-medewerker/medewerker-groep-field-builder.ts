/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';

import {Group} from '../../../../identity/model/group';
import {User} from '../../../../identity/model/user';
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

    groepLabel(groepLabel: string): this {
        this.formField.groepLabel = groepLabel;
        return this;
    }

    defaultGroep(groep: Group): this {
        this.formField.defaultGroep = groep;
        return this;
    }

    medewerkerLabel(medewerkerLabel: string): this {
        this.formField.medewerkerLabel = medewerkerLabel;
        return this;
    }

    defaultMedewerker(medewerker: User): this {
        this.formField.defaultMedewerker = medewerker;
        return this;
    }

    build() {
        return this.formField;
    }

}
