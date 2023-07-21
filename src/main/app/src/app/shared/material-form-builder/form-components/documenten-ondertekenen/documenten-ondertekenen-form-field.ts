/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FieldType } from "../../model/field-type.enum";
import { DocumentenLijstFormField } from "../documenten-lijst/documenten-lijst-form-field";

export class DocumentenOndertekenenFormField extends DocumentenLijstFormField {
  fieldType = FieldType.DOCUMENTEN_ONDERTEKENEN;

  constructor() {
    super();
  }
}
