/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input } from "@angular/core";
import { EditComponent } from "../edit.component";
import { TextareaFormField } from "../../material-form-builder/form-components/textarea/textarea-form-field";
import { MaterialFormBuilderService } from "../../material-form-builder/material-form-builder.service";
import { UtilService } from "../../../core/service/util.service";
import { InputFormField } from "../../material-form-builder/form-components/input/input-form-field";
import { Validators } from "@angular/forms";

@Component({
  selector: "zac-edit-tekst",
  templateUrl: "./edit-tekst.component.html",
  styleUrls: [
    "../../static-text/static-text.component.less",
    "../edit.component.less",
  ],
})
export class EditTekstComponent extends EditComponent {
  @Input() formField: TextareaFormField;
  @Input() reasonField: InputFormField;

  constructor(
    mfbService: MaterialFormBuilderService,
    utilService: UtilService,
  ) {
    super(mfbService, utilService);
  }

  edit(): void {
    super.edit();

    if (this.reasonField) {
      this.formFields.setControl("reden", this.reasonField.formControl);
    }
  }
}
