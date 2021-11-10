/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {ReadonlyFormField} from './readonly-form-field';

@Component({
    templateUrl: './readonly.component.html',
    styleUrls: ['./readonly.component.less']
})
export class ReadonlyComponent implements OnInit, IFormComponent {

    data: ReadonlyFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
