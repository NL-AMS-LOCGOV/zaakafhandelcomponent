/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'zac-tekst-filter',
    templateUrl: './tekst-filter.component.html',
    styleUrls: ['./tekst-filter.component.less']
})
export class TekstFilterComponent {
    @Input('value') model: string;
    @Output() changed = new EventEmitter<string>();
}
