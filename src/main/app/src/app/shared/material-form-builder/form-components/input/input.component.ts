/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {InputFormField} from './input-form-field';
import {IFormComponent} from '../../model/iform-component';

@Component({
    templateUrl: './input.component.html',
    styleUrls: ['./input.component.less']
})
export class InputComponent implements OnInit, IFormComponent {

    data: InputFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
