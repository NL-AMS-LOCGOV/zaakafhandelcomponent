/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, Output } from "@angular/core";
import { ToggleSwitchOptions } from "./toggle-switch-options";

@Component({
  selector: "zac-toggle-filter",
  templateUrl: "./toggle-filter.component.html",
  styleUrls: ["./toggle-filter.component.less"],
})
export class ToggleFilterComponent {
  @Input() selected: ToggleSwitchOptions = ToggleSwitchOptions.INDETERMINATE;
  @Input() checkedIcon = "check_circle";
  @Input() unCheckedIcon = "cancel";
  @Input() indeterminateIcon = "radio_button_unchecked";
  @Output() changed = new EventEmitter<ToggleSwitchOptions>();

  readonly toggleSwitchOptions = ToggleSwitchOptions;

  toggle() {
    switch (this.selected) {
      case ToggleSwitchOptions.CHECKED:
        this.selected = ToggleSwitchOptions.UNCHECKED;
        break;
      case ToggleSwitchOptions.UNCHECKED:
        this.selected = ToggleSwitchOptions.INDETERMINATE;
        break;
      case ToggleSwitchOptions.INDETERMINATE:
        this.selected = ToggleSwitchOptions.CHECKED;
        break;
    }

    this.changed.emit(this.selected);
  }
}
