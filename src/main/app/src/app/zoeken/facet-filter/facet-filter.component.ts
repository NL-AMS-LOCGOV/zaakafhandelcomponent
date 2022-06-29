/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'zac-facet-filter',
    templateUrl: './facet-filter.component.html',
    styleUrls: ['./facet-filter.component.less']
})
export class FacetFilterComponent {
    @Input() selected: string;
    @Input() opties: string[];
    @Input() label: string;
    @Output() changed = new EventEmitter<string>();

    getFilters(): string[] {
        if (this.opties) {
            return this.opties.sort((a, b) => a.localeCompare(b));
        }
    }
}
