/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {SelectFormField} from './select-form-field';

@Component({
    templateUrl: './select.component.html',
    styleUrls: ['./select.component.less']
})
export class SelectComponent implements OnInit, IFormComponent {

    data: SelectFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
