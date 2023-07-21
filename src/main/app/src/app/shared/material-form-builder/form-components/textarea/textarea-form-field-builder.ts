/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { TextareaFormField } from "./textarea-form-field";

export class TextareaFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: TextareaFormField;

  constructor(value?: any) {
    super();
    this.formField = new TextareaFormField();
    this.formField.initControl(value);
  }

  maxlength(maxlength: number, showCount = true): this {
    this.formField.maxlength = maxlength;
    this.formField.showCount = showCount;
    return this;
  }
}
