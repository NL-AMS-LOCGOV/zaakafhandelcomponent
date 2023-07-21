/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { RadioFormField } from "./radio-form-field";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./radio.component.html",
  styleUrls: ["./radio.component.less"],
})
export class RadioComponent extends FormComponent implements OnInit {
  data: RadioFormField;
  selectedValue: string;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {
    this.selectedValue = this.data.formControl.value;
  }

  radioChanged(): void {
    this.data.formControl.setValue(this.selectedValue);
  }
}
