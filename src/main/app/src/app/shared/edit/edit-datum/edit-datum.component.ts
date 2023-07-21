/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input } from "@angular/core";
import { EditComponent } from "../edit.component";
import { MaterialFormBuilderService } from "../../material-form-builder/material-form-builder.service";
import { DateFormField } from "../../material-form-builder/form-components/date/date-form-field";
import { UtilService } from "../../../core/service/util.service";

@Component({
  selector: "zac-edit-datum",
  templateUrl: "./edit-datum.component.html",
  styleUrls: [
    "../../static-text/static-text.component.less",
    "../edit.component.less",
  ],
})
export class EditDatumComponent extends EditComponent {
  @Input() formField: DateFormField;

  constructor(
    mfbService: MaterialFormBuilderService,
    utilService: UtilService,
  ) {
    super(mfbService, utilService);
  }
}
