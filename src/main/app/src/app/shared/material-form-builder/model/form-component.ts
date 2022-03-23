/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormField} from './abstract-form-field';
import {TranslateService} from '@ngx-translate/core';

export abstract class FormComponent {

    abstract translate: TranslateService;
    data: AbstractFormField;

    constructor() {
    }

    getErrorMessage(): string {
        const formControl = this.data.formControl;
        if (formControl.hasError('required')) {
            return this.labeled('msg.error.required', {});
        } else if (formControl.hasError('min')) {
            return this.labeled('msg.error.teklein', {
                min: formControl.errors.min.min,
                actual: formControl.errors.min.actual
            });
        } else if (formControl.hasError('max')) {
            return this.labeled('msg.error.tegroot', {
                max: formControl.errors.max.max,
                actual: formControl.errors.max.actual
            });
        } else if (formControl.hasError('minlength')) {
            return this.labeled('msg.error.tekort', {
                requiredLength: formControl.errors.minlength.requiredLength,
                actualLength: formControl.errors.minlength.actualLength
            });
        } else if (formControl.hasError('maxlength')) {
            return this.labeled('msg.error.telang', {
                requiredLength: formControl.errors.maxlength.requiredLength,
                actualLength: formControl.errors.maxlength.actualLength
            });
        } else if (formControl.hasError('email')) {
            return this.labeled('msg.error.invalid.email', {});
        } else if (this.data.formControl.hasError('pattern')) {
            return this.labeled('msg.error.invalid.formaat', {
                requiredPattern: formControl.errors.pattern.requiredPattern,
                actualValue: formControl.errors.pattern.actualValue
            });
        } else if (formControl.hasError('bsn')) {
            return this.labeled('msg.error.invalid.bsn', {});
        } else if (formControl.hasError('bsnOrVestiging')) {
            return this.labeled('msg.error.invalid.bsnOrVes', {});
        } else if (formControl.hasError('postcode')) {
            return this.labeled('msg.error.invalid.postcode', {});
        } else {
            return '';
        }
    }

    labeled(key: string, params: object): string {
        if (this.data.label) {
            params['label'] = this.translate.instant(this.data.label);
        }
        return this.translate.instant(key, params);
    }
}
