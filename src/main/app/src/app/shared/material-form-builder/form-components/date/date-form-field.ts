/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FieldType } from "../../model/field-type.enum";
import { AbstractFormControlField } from "../../model/abstract-form-control-field";

export class DateFormField extends AbstractFormControlField {
  fieldType = FieldType.DATE;
  public minDate: Date;
  public maxDate: Date;
  public showDays: boolean;

  constructor() {
    super();
  }
}
