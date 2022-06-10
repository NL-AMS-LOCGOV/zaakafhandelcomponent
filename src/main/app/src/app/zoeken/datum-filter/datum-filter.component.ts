/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'zac-datum-filter',
    templateUrl: './datum-filter.component.html',
    styleUrls: ['./datum-filter.component.less']
})
export class DatumFilterComponent {
    @Input() range: { van, tot };
    @Input() label: string;
    @Output() changed = new EventEmitter<void>();

    clearDate($event: MouseEvent): void {
        $event.stopPropagation();
        this.range.van = null;
        this.range.tot = null;
        this.changed.emit();
    }

    hasDate(): boolean {
        return this.range.van != null;
    }
}
