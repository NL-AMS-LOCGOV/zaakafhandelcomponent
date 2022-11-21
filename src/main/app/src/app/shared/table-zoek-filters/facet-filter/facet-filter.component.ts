/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FilterResultaat} from '../../../zoeken/model/filter-resultaat';

@Component({
    selector: 'zac-facet-filter',
    templateUrl: './facet-filter.component.html',
    styleUrls: ['./facet-filter.component.less']
})
export class FacetFilterComponent implements OnInit, OnChanges {
    _selected: string;
    @Input() selected: string[];
    @Input() opties: FilterResultaat[];
    @Input() label: string;
    @Output() changed = new EventEmitter<string[]>();

    getFilters(): FilterResultaat[] {
        if (this.opties) {
            return this.opties.sort((a, b) => a.naam.localeCompare(b.naam));
        }
    }

    ngOnInit(): void {
        this.setSelected();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.selected && !changes.selected.firstChange) {
            this.setSelected();
        }
    }

    private setSelected(): void {
        this._selected = this.selected ? this.selected[0] : null;
    }
}
