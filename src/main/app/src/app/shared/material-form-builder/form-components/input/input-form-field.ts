/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FieldType } from "../../model/field-type.enum";
import { ActionIcon } from "../../../edit/action-icon";
import { AbstractFormControlField } from "../../model/abstract-form-control-field";
import { Subject } from "rxjs";
import { EventEmitter } from "@angular/core";

export class InputFormField extends AbstractFormControlField {
  fieldType: FieldType = FieldType.INPUT;
  icons: ActionIcon[];
  clicked: Subject<any> = new Subject<any>();
  onClear = new EventEmitter<void>();
  maxlength: number;
  showCount = false;

  constructor() {
    super();
  }
}
