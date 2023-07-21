/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { DateFormField } from "./date-form-field";
import { TranslateService } from "@ngx-translate/core";
import * as moment from "moment";

@Component({
  templateUrl: "./date.component.html",
  styleUrls: ["./date.component.less"],
})
export class DateComponent extends FormComponent implements OnInit {
  data: DateFormField;

  constructor(public translate: TranslateService) {
    super();
  }

  getErrorMessage(): string {
    const formControl = this.data.formControl;
    if (formControl.hasError("matDatepickerParse")) {
      return this.translate.instant("msg.error.invalid.formaat", {
        label: this.translate.instant(this.data.label),
        requiredPattern: this.translate.instant("msg.error.date.formaat"),
      });
    }
    return super.getErrorMessage();
  }

  ngOnInit(): void {}

  days() {
    return moment(this.data.formControl.value).diff(
      moment().startOf("day"),
      "days",
    );
  }
}
