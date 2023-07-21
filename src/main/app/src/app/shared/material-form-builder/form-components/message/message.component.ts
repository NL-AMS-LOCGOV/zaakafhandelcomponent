/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { MessageFormField } from "./message-form-field";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./message.component.html",
  styleUrls: ["./message.component.less"],
})
export class MessageComponent extends FormComponent {
  data: MessageFormField;

  constructor(public translate: TranslateService) {
    super();
  }
}
