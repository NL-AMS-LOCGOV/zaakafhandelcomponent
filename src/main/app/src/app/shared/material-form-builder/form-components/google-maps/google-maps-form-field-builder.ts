/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { GoogleMapsFormField } from "./google-maps-form-field";

export class GoogleMapsFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: GoogleMapsFormField;

  constructor(value?: any) {
    super();
    this.formField = new GoogleMapsFormField();
    this.formField.initControl(value);
  }
}
