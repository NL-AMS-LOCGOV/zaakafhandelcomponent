/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormControl } from "@angular/forms";
import { AbstractFormField } from "./abstract-form-field";

export abstract class AbstractFormControlField extends AbstractFormField {
  formControl: FormControl;

  protected constructor() {
    super();
  }

  initControl(value?: any): void {
    this.formControl = AbstractFormField.formControlInstance(value);
  }
}
