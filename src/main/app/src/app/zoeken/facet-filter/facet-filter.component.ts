/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'zac-facet-filter',
    templateUrl: './facet-filter.component.html',
    styleUrls: ['./facet-filter.component.less']
})
export class FacetFilterComponent implements OnInit {
    _selected: string;
    @Input() selected: string[];
    @Input() opties: string[];
    @Input() label: string;
    @Output() changed = new EventEmitter<string[]>();

    getFilters(): string[] {
        if (this.opties) {
            return this.opties.sort((a, b) => a.localeCompare(b));
        }
    }

    ngOnInit(): void {
        this._selected = this.selected ? this._selected[0] : null;
    }
}
