/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormControl } from "@angular/forms";
import { DatumRange } from "../../../model/datum-range";

@Component({
  selector: "zac-date-filter",
  templateUrl: "./date-filter.component.html",
  styleUrls: ["./date-filter.component.less"],
})
export class DateFilterComponent implements OnInit {
  @Input() range: DatumRange;
  @Input() label: string;
  @Output() changed = new EventEmitter<DatumRange>();

  dateVan: FormControl<Date> = new FormControl<Date>(null);
  dateTM: FormControl<Date> = new FormControl<Date>(null);

  ngOnInit(): void {
    if (this.range == null) {
      this.range = new DatumRange();
    }
    this.dateVan.setValue(this.range.van);
    this.dateTM.setValue(this.range.tot);
  }

  change(): void {
    this.range.van = this.dateVan.value;
    this.range.tot = this.dateTM.value;
    this.changed.emit(this.range);
  }

  expanded(): boolean {
    return this.range.van != null || this.range.tot != null;
  }
}
