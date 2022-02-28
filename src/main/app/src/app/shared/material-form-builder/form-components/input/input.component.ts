/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {InputFormField} from './input-form-field';
import {FormComponent} from '../../model/form-component';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './input.component.html',
    styleUrls: ['./input.component.less']
})
export class InputComponent extends FormComponent implements OnInit {

    data: InputFormField;

    constructor(public translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
    }

    iconClick($event: MouseEvent): void {
        this.data.iconClicked$.next();
    }
}
