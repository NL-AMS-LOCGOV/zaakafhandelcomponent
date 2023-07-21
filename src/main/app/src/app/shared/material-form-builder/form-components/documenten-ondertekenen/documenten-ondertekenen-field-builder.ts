/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { DocumentenLijstFieldBuilder } from "../documenten-lijst/documenten-lijst-field-builder";
import { DocumentenOndertekenenFormField } from "./documenten-ondertekenen-form-field";

export class DocumentenOndertekenenFieldBuilder extends DocumentenLijstFieldBuilder {
  readonly formField: DocumentenOndertekenenFormField;

  constructor() {
    super();
    this.formField = new DocumentenOndertekenenFormField();
    this.formField.selectLabel = "ondertekenen";
    this.formField.initControl();
  }
}
