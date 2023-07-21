/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FieldType } from "../../model/field-type.enum";
import { AbstractFormControlField } from "../../model/abstract-form-control-field";

export class ParagraphFormField extends AbstractFormControlField {
  fieldType = FieldType.PARAGRAPH;

  constructor() {
    super();
  }
}
