/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { CheckboxFormField } from "./checkbox-form-field";
import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";

export class CheckboxFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: CheckboxFormField;

  constructor(value?: any) {
    super();
    this.formField = new CheckboxFormField();
    this.formField.initControl(value);
  }
}
