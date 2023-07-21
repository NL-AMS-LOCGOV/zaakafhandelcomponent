/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FieldType } from "../../model/field-type.enum";
import { AbstractChoicesFormField } from "../../model/abstract-choices-form-field";

export class SelectFormField extends AbstractChoicesFormField {
  fieldType = FieldType.SELECT;

  constructor() {
    super();
  }
}
