/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { HeadingFormField, HeadingLevel } from "./heading-form-field";

export class HeadingFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: HeadingFormField;

  constructor() {
    super();
    this.formField = new HeadingFormField();
  }

  level(level: HeadingLevel): this {
    this.formField.level = level;
    return this;
  }

  validate(): void {
    if (this.formField.label == null) {
      throw new Error("label is required");
    }
    if (this.formField.level == null) {
      throw new Error("level is required");
    }
  }
}
