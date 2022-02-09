/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatSelectionListChange} from '@angular/material/list';
import {ColumnPickerValue} from './column-picker-value';

@Component({
    selector: 'zac-column-picker',
    templateUrl: './column-picker.component.html',
    styleUrls: ['./column-picker.component.less']
})
export class ColumnPickerComponent {

    @Input() set columnSrc(columns: Map<string, ColumnPickerValue>) {
        this._columnSrc = columns;
        this._columns = new Map([...columns.keys()]
        .filter(key => columns.get(key) !== ColumnPickerValue.STICKY)
        .map(key => [key, columns.get(key) === ColumnPickerValue.VISIBLE]));
    }

    @Output() columnsChanged = new EventEmitter<Map<string, ColumnPickerValue>>();

    private _columnSrc: Map<string, ColumnPickerValue>;
    private _columns: Map<string, boolean>;
    private changed: boolean = false;

    constructor() { }

    menuOpened(): void {
        this.changed = false;
    }

    selectionChanged($event: MatSelectionListChange): void {
        this.changed = true;
        $event.options.forEach(
            option => this._columnSrc.set(option.value, this._columnSrc.get(
                option.value) === ColumnPickerValue.VISIBLE ? ColumnPickerValue.HIDDEN : ColumnPickerValue.VISIBLE));
    }

    updateColumns(): void {
        if (this.changed) {
            this.columnsChanged.emit(this._columnSrc);
        }
    }

    get columns(): Map<string, boolean> {
        return this._columns;
    }
}
