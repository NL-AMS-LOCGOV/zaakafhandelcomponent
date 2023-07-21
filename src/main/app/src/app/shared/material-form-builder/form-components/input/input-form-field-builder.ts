/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { InputFormField } from "./input-form-field";
import { ActionIcon } from "../../../edit/action-icon";

export class InputFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: InputFormField;

  constructor(value?: any) {
    super();
    this.formField = new InputFormField();
    this.formField.initControl(value);
  }

  icon(icon: ActionIcon): this {
    this.formField.icons = [icon];
    return this;
  }

  icons(icons: ActionIcon[]): this {
    this.formField.icons = icons;
    return this;
  }

  disabled() {
    this.formField.formControl.disable();
    return this;
  }

  maxlength(maxlength: number, showCount = true): this {
    this.formField.maxlength = maxlength;
    this.formField.showCount = showCount;
    return this;
  }
}
