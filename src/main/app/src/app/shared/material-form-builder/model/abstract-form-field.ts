/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormControl, FormControlOptions} from '@angular/forms';
import {FieldType} from './field-type.enum';
import {FormFieldHint} from './form-field-hint';
import {first, isObservable, Observable} from 'rxjs';

export abstract class AbstractFormField {
    id: string;
    label: string;
    required: boolean;
    readonly: boolean;
    formControl: FormControl;
    hint: FormFieldHint;
    private formControlOptions: FormControlOptions = {initialValueIsDefault: true};

    abstract fieldType: FieldType;

    protected constructor() {
    }

    hasReadonlyView() {
        return false;
    }

    value(value:any){
        this.formControl.setValue(value);
        this.formControl.markAsDirty();
    }

    reset(): void {
        this.formControl.reset();
    }

    initFormControl(value?: any | Observable<any>): void {
        if (isObservable(value)) {
            value.pipe(first()).subscribe(firstValue => {
                this.formControl = new FormControl(firstValue, this.formControlOptions);
            });
        } else {
            this.formControl = new FormControl(value, this.formControlOptions);
        }
    }
}
