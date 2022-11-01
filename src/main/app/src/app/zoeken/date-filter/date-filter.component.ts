/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
    selector: 'zac-date-filter',
    templateUrl: './date-filter.component.html',
    styleUrls: ['./date-filter.component.less']
})
export class DateFilterComponent implements OnInit {
    @Input() range: { van: Date, tot: Date };
    @Input() label: string;
    @Output() changed = new EventEmitter<void>();

    dateVan: FormControl<Date>;
    dateTM: FormControl<Date>;

    ngOnInit(): void {
        this.dateVan = new FormControl(this.range.van);
        this.dateTM = new FormControl(this.range.tot);
    }

    change(): void {
        this.range.van = this.dateVan.value;
        this.range.tot = this.dateTM.value;
        this.changed.emit();
    }

    expanded(): boolean {
        return this.range.van != null || this.range.tot != null;
    }
}
