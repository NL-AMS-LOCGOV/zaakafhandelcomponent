/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ParagraphFormField } from "./paragraph-form-field";

export class ParagraphFormFieldBuilder {
  protected readonly formField: ParagraphFormField;

  constructor() {
    this.formField = new ParagraphFormField();
  }

  validate(): void {
    if (this.formField.label == null) {
      throw new Error("label is required");
    }
  }

  text(text: string): this {
    this.formField.label = text;
    return this;
  }

  build() {
    this.validate();
    return this.formField;
  }
}
