/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FormControl} from '@angular/forms';
import {FieldType} from './field-type.enum';
import {FormFieldHint} from './form-field-hint';
import {TranslateService} from '@ngx-translate/core';

export abstract class AbstractFormField {
    id: string;
    label: string;
    required: boolean;
    readonly: boolean;
    readonly formControl: FormControl = new FormControl();
    hint: FormFieldHint;

    abstract fieldType: FieldType;

    protected constructor(protected translate: TranslateService) {
    }

    getErrorMessage(): string {
        if (this.formControl.hasError('required')) {
            return this.labeled('msg.error.required', {});
        } else if (this.formControl.hasError('min')) {
            return this.labeled('msg.error.teklein', {
                min: this.formControl.errors.min.min,
                actual: this.formControl.errors.min.actual
            });
        } else if (this.formControl.hasError('max')) {
            return this.labeled('msg.error.tegroot', {
                max: this.formControl.errors.max.max,
                actual: this.formControl.errors.max.actual
            });
        } else if (this.formControl.hasError('minlength')) {
            return this.labeled('msg.error.tekort', {
                requiredLength: this.formControl.errors.minlength.requiredLength,
                actualLength: this.formControl.errors.minlength.actualLength
            });
        } else if (this.formControl.hasError('maxlength')) {
            return this.labeled('msg.error.telang', {
                requiredLength: this.formControl.errors.maxlength.requiredLength,
                actualLength: this.formControl.errors.maxlength.actualLength
            });
        } else if (this.formControl.hasError('email')) {
            return this.labeled('msg.error.invalid.email', {});
        } else if (this.formControl.hasError('pattern')) {
            return this.labeled('msg.error.invalid.formaat', {
                requiredPattern: this.formControl.errors.pattern.requiredPattern,
                actualValue: this.formControl.errors.pattern.actualValue
            });
        } else {
            return '';
        }
    }

    private labeled(key: string, params: object): string {
        if (this.label) {
            params['label'] = this.translate.instant(this.label);
        }
        return this.translate.instant(key, params);
    }

    hasReadonlyView() {
        return false;
    }
}
