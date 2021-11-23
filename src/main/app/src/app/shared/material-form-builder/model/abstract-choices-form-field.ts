/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from './abstract-form-field';
import {FormFieldConfig} from './form-field-config';
import {Observable} from 'rxjs';

/**
 * Abstract class voor Form Fields die meerdere waardes tonen (checkbox, radiobutton, select)
 * Deze componenten hebben een compare methode nodig om te bepalen welke value geselecteerd moet worden in de lijst.
 */
export abstract class AbstractChoicesFormField extends AbstractFormField {

    public options: Observable<any[]>;
    public optionLabel: string | null;

    protected constructor(id: string, label: string, value: any, optionLabel: string | null, options: Observable<any[]>, config?: FormFieldConfig) {
        super(id, label, value, config);
        this.optionLabel = optionLabel;
        this.options = options;
    }

    compareWithFn = (object1: any, object2: any): boolean => {
        if (object1 && object2) {
            return this.optionLabel ? object1[this.optionLabel] === object2[this.optionLabel] : object1 === object2;
        }
        return false;
    };
}
