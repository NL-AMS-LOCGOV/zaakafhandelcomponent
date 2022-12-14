/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Observable, tap} from 'rxjs';
import {EventEmitter} from '@angular/core';
import {AbstractFormControlField} from './abstract-form-control-field';
import {OrderUtil} from '../../order/order-util';

/**
 * Abstract class voor Form Fields die meerdere waardes tonen (checkbox, radiobutton, select)
 * Deze componenten hebben een compare methode nodig om te bepalen welke value geselecteerd moet worden in de lijst.
 */
export abstract class AbstractChoicesFormField extends AbstractFormControlField {

    optionsChanged$ = new EventEmitter<void>();
    private options$: Observable<any[]>;
    public optionLabel: string | null;
    loading$ = new EventEmitter<boolean>();

    protected constructor() {
        super();
    }

    compareWithFn = (object1: any, object2: any): boolean => {
        if (object1 && object2) {
            return this.optionLabel ? object1[this.optionLabel] === object2[this.optionLabel] : object1 === object2;
        }
        return false;
    };

    get options(): Observable<any[]> {
        return this.options$;
    }

    set options(options: Observable<any[]>) {
        this.options$ = options.pipe(
            tap({
                subscribe: () => this.loading$.emit(true),
                next: () => this.loading$.emit(false)
            }), tap(value => value.sort(OrderUtil.orderBy(this.optionLabel))));
        this.optionsChanged$.next();
    }
}
