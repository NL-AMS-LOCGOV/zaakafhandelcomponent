/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Type} from '@angular/core';
import {IFormComponent} from './iform-component';
import {AbstractFormField} from './abstract-form-field';

export class FormItem {
    constructor(public component: Type<IFormComponent>, public data: AbstractFormField) {
    }
}
