/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormFieldBuilder} from '../../model/abstract-form-field-builder';
import {DocumentenLijstFormField} from './documenten-lijst-form-field';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../../../informatie-objecten/model/enkelvoudig-informatieobject';

export class DocumentenLijstFieldBuilder extends AbstractFormFieldBuilder {

    readonly formField: DocumentenLijstFormField;

    constructor(value?: any) {
        super();
        this.formField = new DocumentenLijstFormField();
        this.formField.initControl(value);
    }

    documenten(documenten$: Observable<EnkelvoudigInformatieobject[]>): this {
        this.formField.documenten$ = documenten$;
        return this;
    }

    documentenCheckedVoorOndertekenen(documentenChecked: string[]): this {
        this.formField.documentenCheckedVoorOndertekenen = documentenChecked;
        return this;
    }

    documentenChecked(documentenChecked: string[]): this {
        this.formField.documentenChecked = documentenChecked;
        return this;
    }

    ondertekenen(ondertekenen: boolean): this {
        this.formField.ondertekenen = ondertekenen;
        return this;
    }

    verbergStatus(): this {
        this.formField.verbergStatus = true;
        return this;
    }
}
