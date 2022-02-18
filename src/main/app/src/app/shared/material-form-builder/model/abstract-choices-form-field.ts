/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from './abstract-form-field';
import {Observable, Subject} from 'rxjs';

/**
 * Abstract class voor Form Fields die meerdere waardes tonen (checkbox, radiobutton, select)
 * Deze componenten hebben een compare methode nodig om te bepalen welke value geselecteerd moet worden in de lijst.
 */
export abstract class AbstractChoicesFormField extends AbstractFormField {

    private optionsChanged$ = new Subject<void>();
    private options$: Observable<any[]>;
    public optionLabel: string | null;

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
        this.options$ = options;
        this.optionsChanged$.next();
    }

    get optionsChanged(): Observable<void> {
        return this.optionsChanged$.asObservable();
    }
}
