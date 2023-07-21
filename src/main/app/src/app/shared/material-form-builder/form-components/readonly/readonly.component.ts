/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { ReadonlyFormField } from "./readonly-form-field";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./readonly.component.html",
  styleUrls: ["./readonly.component.less"],
})
export class ReadonlyComponent extends FormComponent implements OnInit {
  data: ReadonlyFormField;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {}
}
