/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractChoicesFormField } from "../../model/abstract-choices-form-field";
import { FieldType } from "../../model/field-type.enum";

export class AutocompleteFormField extends AbstractChoicesFormField {
  fieldType = FieldType.AUTOCOMPLETE;
  maxlength: number;

  constructor() {
    super();
  }
}
