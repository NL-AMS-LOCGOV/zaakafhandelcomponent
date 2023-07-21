/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";

@Component({
  selector: "zac-tekst-filter",
  templateUrl: "./tekst-filter.component.html",
  styleUrls: ["./tekst-filter.component.less"],
})
export class TekstFilterComponent implements OnInit {
  oldValue: string;
  @Input() value: string;
  @Output() changed = new EventEmitter<string>();

  ngOnInit(): void {
    this.oldValue = this.value;
  }

  change(): void {
    if (this.oldValue !== this.value) {
      this.oldValue = this.value;
      this.changed.emit(this.value);
    }
  }
}
