/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {TextareaFormField} from './textarea-form-field';

@Component({
    selector: 'mfb-textarea',
    templateUrl: './textarea.component.html',
    styleUrls: ['./textarea.component.less']
})
export class TextareaComponent implements OnInit, IFormComponent {
    data: TextareaFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
