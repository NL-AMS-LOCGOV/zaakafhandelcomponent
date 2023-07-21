/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { ReadonlyFormField } from "./readonly-form-field";

export class ReadonlyFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: ReadonlyFormField;

  constructor(value?: any) {
    super();
    this.formField = new ReadonlyFormField();
    this.formField.initControl(value);
  }
}
