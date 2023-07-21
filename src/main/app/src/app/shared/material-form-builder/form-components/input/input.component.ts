/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { InputFormField } from "./input-form-field";
import { FormComponent } from "../../model/form-component";
import { TranslateService } from "@ngx-translate/core";
import { ActionIcon } from "../../../edit/action-icon";

@Component({
  templateUrl: "./input.component.html",
  styleUrls: ["./input.component.less"],
})
export class InputComponent extends FormComponent implements OnInit {
  data: InputFormField;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {}

  iconClick($event: MouseEvent, icon: ActionIcon): void {
    icon.iconClicked.next(null);
  }

  clicked(): void {
    if (this.data.clicked.observed) {
      this.data.clicked.next(null);
    }
  }
}
