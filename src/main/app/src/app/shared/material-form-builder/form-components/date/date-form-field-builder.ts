/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { DateFormField } from "./date-form-field";

export class DateFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: DateFormField;

  constructor(value?: any) {
    super();
    this.formField = new DateFormField();
    this.formField.initControl(value);
    const maxDate = new Date();
    maxDate.setDate(maxDate.getDate() + 36525); // default maxDate (100 jaar)
    this.maxDate(maxDate);
  }

  minDate(date: Date) {
    this.formField.minDate = date;
    return this;
  }

  maxDate(date: Date) {
    this.formField.maxDate = date;
    return this;
  }

  showDays() {
    this.formField.showDays = true;
    return this;
  }
}
