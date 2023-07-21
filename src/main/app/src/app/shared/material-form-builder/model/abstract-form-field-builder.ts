/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormField } from "./abstract-form-field";
import { ValidatorFn, Validators } from "@angular/forms";
import { FormFieldHint } from "./form-field-hint";
import { first, Observable } from "rxjs";

export abstract class AbstractFormFieldBuilder {
  abstract readonly formField: AbstractFormField;

  protected constructor() {}

  id(id: string): this {
    this.formField.id = id;
    return this;
  }

  styleClass(styleClass: string): this {
    this.formField.styleClass = styleClass;
    return this;
  }

  label(label: string): this {
    this.formField.label = label;
    return this;
  }

  readonly(readonly: boolean): this {
    this.formField.readonly = readonly;
    return this;
  }

  value$(value: Observable<any>): this {
    value.pipe(first()).subscribe((firstValue) => {
      this.formField.formControl.setValue(firstValue);
    });
    return this;
  }

  validators(...validators: ValidatorFn[]): this {
    this.formField.formControl.setValidators(validators);
    if (
      validators.find((v) => v === Validators.required) ||
      validators.find((v) => v === Validators.requiredTrue)
    ) {
      this.formField.required = true;
    }
    return this;
  }

  hint(hint: string, align?: "start" | "end"): this {
    this.formField.hint = new FormFieldHint(hint, align ? align : "end");
    return this;
  }

  build(): this["formField"] {
    this.validate();
    return this.formField;
  }

  validate() {
    if (!this.formField.id) {
      throw new Error("id is required");
    }
    if (!this.formField.label) {
      throw new Error("label is required");
    }
  }
}
