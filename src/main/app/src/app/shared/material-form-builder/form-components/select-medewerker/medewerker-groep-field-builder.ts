/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {MedewerkerGroepFormField} from './medewerker-groep-form-field';

export class MedewerkerGroepFieldBuilder extends AbstractFormFieldBuilder {

    protected readonly formField: MedewerkerGroepFormField;

    constructor(value?: any) {
        super();
        this.formField = new MedewerkerGroepFormField();
        this.formField.initFormControl(value);
    }

    groepOptioneel(): this {
        this.formField.groepOptioneel = true;
        return this;
    }

    groepLabel(groepLabel: string): this {
        this.formField.groepLabel = groepLabel;
        return this;
    }

    defaultGroep(groepId: string): this {
        this.formField.defaultGroepId = groepId;
        return this;
    }

    medewerkerLabel(medewerkerLabel: string): this {
        this.formField.medewerkerLabel = medewerkerLabel;
        return this;
    }

    defaultMedewerker(medewerkerId: string): this {
        this.formField.defaultMedewerkerId = medewerkerId;
        return this;
    }

    maxlength(maxlength: number): this {
        this.formField.maxlength = maxlength;
        return this;
    }

    build() {
        return this.formField;
    }

}
