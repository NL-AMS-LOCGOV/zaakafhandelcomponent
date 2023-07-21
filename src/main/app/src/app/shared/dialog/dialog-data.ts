/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Observable } from "rxjs";
import { AbstractFormField } from "../material-form-builder/model/abstract-form-field";

export class DialogData {
  public confirmButtonActionKey = "actie.ja";
  public cancelButtonActionKey = "actie.annuleren";

  constructor(
    public formFields: AbstractFormField[],
    public fn?: (results: any[]) => Observable<any>,
    public melding?: string,
    public uitleg?: string,
  ) {}

  formFieldsInvalid(): boolean {
    return this.formFields.some((field) => field.formControl.invalid);
  }
}
