/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractChoicesFormFieldBuilder } from "../../model/abstract-choices-form-field-builder";
import { RadioFormField } from "./radio-form-field";

export class RadioFormFieldBuilder extends AbstractChoicesFormFieldBuilder {
  readonly formField: RadioFormField;

  constructor(value?: any) {
    super();
    this.formField = new RadioFormField();
    this.formField.initControl(value);
  }
}
