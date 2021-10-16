/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {DateFormField} from './date-form-field';

@Component({
    selector: 'mfb-date',
    templateUrl: './date.component.html',
    styleUrls: ['./date.component.less']
})
export class DateComponent implements OnInit, IFormComponent {

    data: DateFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
