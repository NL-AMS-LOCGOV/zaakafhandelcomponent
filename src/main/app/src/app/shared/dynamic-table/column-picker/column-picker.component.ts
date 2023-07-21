/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MatSelectionListChange } from "@angular/material/list";
import { ColumnPickerValue } from "./column-picker-value";
import { TranslateService } from "@ngx-translate/core";

@Component({
  selector: "zac-column-picker",
  templateUrl: "./column-picker.component.html",
  styleUrls: ["./column-picker.component.less"],
})
export class ColumnPickerComponent {
  @Input() set columnSrc(columns: Map<string, ColumnPickerValue>) {
    this._selection = [];
    this._columnSrc = columns;
    this._columns = new Map(
      [...columns.keys()]
        .filter((key) => columns.get(key) !== ColumnPickerValue.STICKY)
        .map((key) => {
          if (columns.get(key) === ColumnPickerValue.VISIBLE) {
            this._selection.push(key);
          }
          return [key, this.translate.instant(key)];
        }),
    );
  }

  @Output() columnsChanged = new EventEmitter<Map<string, ColumnPickerValue>>();

  private _columnSrc: Map<string, ColumnPickerValue>;
  private _columns: Map<string, string>;
  private _selection: string[];
  private changed = false;

  constructor(private translate: TranslateService) {}

  menuOpened(): void {
    this.changed = false;
  }

  selectionChanged($event: MatSelectionListChange): void {
    this.changed = true;
    $event.options.forEach((option) =>
      this._columnSrc.set(
        option.value,
        this._columnSrc.get(option.value) === ColumnPickerValue.VISIBLE
          ? ColumnPickerValue.HIDDEN
          : ColumnPickerValue.VISIBLE,
      ),
    );
  }

  updateColumns(): void {
    if (this.changed) {
      this.columnsChanged.emit(this._columnSrc);
    }
  }

  get columns(): Map<string, string> {
    return this._columns;
  }

  isSelected(column: string): boolean {
    return this._selection.includes(column);
  }
}
