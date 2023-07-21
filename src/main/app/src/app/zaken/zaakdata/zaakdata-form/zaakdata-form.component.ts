/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input } from "@angular/core";
import { FormArray, FormControl, FormGroup } from "@angular/forms";

@Component({
  selector: "zac-zaakdata-form",
  templateUrl: "./zaakdata-form.component.html",
  styleUrls: ["./zaakdata-form.component.less"],
})
export class ZaakdataFormComponent {
  @Input() formItem: any;
  @Input() label: string;

  constructor() {}

  getType(): "GROUP" | "CONTROL" | "ARRAY" {
    if (this.formItem instanceof FormControl) {
      return "CONTROL";
    }
    if (this.formItem instanceof FormGroup) {
      return "GROUP";
    }
    if (this.formItem instanceof FormArray) {
      return "ARRAY";
    }
  }

  getControl(): FormControl {
    return this.formItem as FormControl;
  }

  getGroup(): FormGroup {
    return this.formItem as FormGroup;
  }

  getArray() {
    return this.formItem as FormArray<FormControl>;
  }
}
