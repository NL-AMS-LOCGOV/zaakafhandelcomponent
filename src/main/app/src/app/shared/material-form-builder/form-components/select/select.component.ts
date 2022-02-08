/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormComponent} from '../../model/form-component';
import {SelectFormField} from './select-form-field';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './select.component.html',
    styleUrls: ['./select.component.less']
})
export class SelectComponent extends FormComponent implements OnInit {

    data: SelectFormField;

    constructor(public translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
    }

}
