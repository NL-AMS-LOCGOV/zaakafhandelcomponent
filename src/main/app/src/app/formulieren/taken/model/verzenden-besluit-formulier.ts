/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormGroup, Validators} from '@angular/forms';
import {RadioFormFieldBuilder} from '../../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {Observable, of} from 'rxjs';
import {AbstractFormField} from '../../../shared/material-form-builder/model/abstract-form-field';

export class VerzendenBesluitFormulier {

    private readonly VERZEND_METHODE_PREFIX: string = 'verzendMethode.';

    private readonly fields = {
        VERZEND_METHODE: 'verzend_methode'
    };

    private readonly verzendMethoden = [
        'mail', 'mijn_gemeente', 'post', 'berichtenbox'
    ];

    form: Array<AbstractFormField[]>;

    initForm() {
        this.form = [];
        this.form.push(
            [new RadioFormFieldBuilder()
            .id(this.fields.VERZEND_METHODE)
            .label(this.fields.VERZEND_METHODE)
            .options(this.getVerzendMethodeOpties$())
            .validators(Validators.required)
            .build()]
        );
    }

    getData(formGroup: FormGroup): {} {
        const data = {};
        data[this.fields.VERZEND_METHODE] = formGroup.controls[this.fields.VERZEND_METHODE].value.substring(
            this.VERZEND_METHODE_PREFIX.length);
        return data;
    }

    private getVerzendMethodeOpties$(): Observable<string[]> {
        return of(this.verzendMethoden.map(verzendMethode => this.VERZEND_METHODE_PREFIX + verzendMethode));
    }
}
