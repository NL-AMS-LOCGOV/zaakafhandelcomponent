/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractChoicesFormFieldBuilder } from "../../model/abstract-choices-form-field-builder";
import { AutocompleteFormField } from "./autocomplete-form-field";

export class AutocompleteFormFieldBuilder extends AbstractChoicesFormFieldBuilder {
  readonly formField: AutocompleteFormField;

  constructor(value?: any) {
    super();
    this.formField = new AutocompleteFormField();
    this.formField.initControl(value);
  }

  maxlength(maxlength: number): this {
    this.formField.maxlength = maxlength;
    return this;
  }
}
