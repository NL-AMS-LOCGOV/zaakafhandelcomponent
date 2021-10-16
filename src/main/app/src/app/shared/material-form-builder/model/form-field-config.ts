/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ValidatorFn} from '@angular/forms';

export class FormFieldConfig {

    hint: FormFieldHint;
    validators: ValidatorFn[];

    constructor(validators?: ValidatorFn[], hint?: FormFieldHint) {
        this.hint = hint;
        this.validators = validators;
    }
}

export class FormFieldHint {
    label: string;
    align: 'start' | 'end';

    constructor(label: string, align?: 'start' | 'end') {
        this.label = label;
        this.align = align ? align : 'end';
    }
}

