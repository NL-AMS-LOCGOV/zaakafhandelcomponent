/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {RadioFormField} from './radio-form-field';

@Component({
    templateUrl: './radio.component.html',
    styleUrls: ['./radio.component.less']
})
export class RadioComponent implements OnInit, IFormComponent {

    data: RadioFormField;
    selectedValue: string;

    constructor() {
    }

    ngOnInit(): void {
        this.selectedValue = this.data.formControl.value;
    }

    radioChanged(): void {
        this.data.formControl.setValue(this.selectedValue);
    }
}
