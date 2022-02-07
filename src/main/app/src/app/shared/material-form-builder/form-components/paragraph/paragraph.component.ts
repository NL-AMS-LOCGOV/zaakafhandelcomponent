/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {IFormComponent} from '../../model/iform-component';
import {ParagraphFormField} from './paragraph-form-field';

@Component({
    templateUrl: './paragraph.component.html',
    styleUrls: ['./paragraph.component.less']
})
export class ParagraphComponent implements OnInit, IFormComponent {

    data: ParagraphFormField;

    constructor() {
    }

    ngOnInit(): void {
    }

}
