/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'zac-date-range-filter',
    templateUrl: './date-range-filter.component.html',
    styleUrls: ['./date-range-filter.component.less']
})
export class DateRangeFilterComponent {
    @Input() range: { van: Date, tot: Date };
    @Input() label: string;
    @Output() changed = new EventEmitter<void>();

    clearDate($event: MouseEvent): void {
        $event.stopPropagation();
        this.range.van = null;
        this.range.tot = null;
        this.changed.emit();
    }

    hasDate(): boolean {
        return this.range.van != null && this.range.tot != null;
    }

    dateChange(): void {
        if (this.hasDate()) {
            this.changed.emit();
        }
    }
}
