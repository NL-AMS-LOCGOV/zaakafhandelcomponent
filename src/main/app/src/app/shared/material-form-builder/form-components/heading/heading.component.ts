/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {HeadingFormField} from './heading-form-field';

@Component({
    selector: 'mfb-heading',
    templateUrl: './heading.component.html',
    styleUrls: ['./heading.component.less']
})
export class HeadingComponent implements OnInit, IFormComponent {

    data: HeadingFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
