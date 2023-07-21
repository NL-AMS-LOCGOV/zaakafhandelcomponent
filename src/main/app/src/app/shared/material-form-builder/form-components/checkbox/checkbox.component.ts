/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { CheckboxFormField } from "./checkbox-form-field";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./checkbox.component.html",
  styleUrls: ["./checkbox.component.less"],
})
export class CheckboxComponent extends FormComponent implements OnInit {
  data: CheckboxFormField;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {}
}
