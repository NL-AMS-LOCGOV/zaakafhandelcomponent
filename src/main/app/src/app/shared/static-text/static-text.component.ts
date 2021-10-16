/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'zac-static-text',
    templateUrl: './static-text.component.html',
    styleUrls: ['./static-text.component.less']
})
export class StaticTextComponent implements OnInit {

    @Input() label: string;
    @Input() value: string;

    constructor() {
    }

    ngOnInit(): void {
    }

}
