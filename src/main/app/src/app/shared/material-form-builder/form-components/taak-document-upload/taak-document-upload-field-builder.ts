/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { TaakDocumentUploadFormField } from "./taak-document-upload-form-field";
import { AbstractFileFormFieldBuilder } from "../../model/abstract-file-form-field-builder";

export class TaakDocumentUploadFieldBuilder extends AbstractFileFormFieldBuilder {
  readonly formField: TaakDocumentUploadFormField;

  constructor(value?: any) {
    super();
    this.formField = new TaakDocumentUploadFormField();
    this.formField.initControl(value);
  }

  defaultTitel(titel: string): this {
    this.formField.defaultTitel = titel;
    return this;
  }

  zaakUUID(zaakUUID: string): this {
    this.formField.zaakUUID = zaakUUID;
    return this;
  }

  validate(): void {
    super.validate();
    if (!this.formField.zaakUUID) {
      throw new Error("zaakUUID is required");
    }
  }
}
