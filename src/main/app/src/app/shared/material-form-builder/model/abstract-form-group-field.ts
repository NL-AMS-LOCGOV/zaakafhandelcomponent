/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormControl, FormGroup } from "@angular/forms";
import { AbstractFormField } from "./abstract-form-field";

export abstract class AbstractFormGroupField extends AbstractFormField {
  formControl: FormGroup;

  protected constructor() {
    super();
  }

  initControl(value: { [key: string]: FormControl<any> }): void {
    this.formControl = new FormGroup(value);
  }
}
