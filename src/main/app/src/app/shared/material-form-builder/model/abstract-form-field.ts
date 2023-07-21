/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AbstractControl,
  FormControl,
  FormControlOptions,
} from "@angular/forms";
import { FieldType } from "./field-type.enum";
import { FormFieldHint } from "./form-field-hint";

export abstract class AbstractFormField {
  static formControlOptions: FormControlOptions = { nonNullable: true };

  id: string;
  styleClass: string;
  label: string;
  required: boolean;
  readonly: boolean;
  abstract formControl: AbstractControl;
  hint: FormFieldHint;

  abstract fieldType: FieldType;

  protected constructor() {}

  hasReadonlyView() {
    return false;
  }

  value(value: any) {
    this.formControl.setValue(value);
    this.formControl.markAsDirty();
  }

  reset(): void {
    this.formControl.reset();
  }

  abstract initControl(value?: any);

  static formControlInstance(value: any): FormControl {
    return new FormControl(value, this.formControlOptions);
  }

  hasFormControl(): boolean {
    return this.formControl != null;
  }
}
