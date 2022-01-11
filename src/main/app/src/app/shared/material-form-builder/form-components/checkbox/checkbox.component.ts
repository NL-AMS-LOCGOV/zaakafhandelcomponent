/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {CheckboxFormField} from './checkbox-form-field';

@Component({
    templateUrl: './checkbox.component.html',
    styleUrls: ['./checkbox.component.less']
})
export class CheckboxComponent implements OnInit, IFormComponent {

    data: CheckboxFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
