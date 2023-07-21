/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { TextareaFormField } from "./textarea-form-field";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./textarea.component.html",
  styleUrls: ["./textarea.component.less"],
})
export class TextareaComponent extends FormComponent implements OnInit {
  data: TextareaFormField;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {}
}
