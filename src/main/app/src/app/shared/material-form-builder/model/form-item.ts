/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Type } from "@angular/core";
import { FormComponent } from "./form-component";
import { AbstractFormField } from "./abstract-form-field";

export class FormItem {
  constructor(
    public component: Type<FormComponent>,
    public data: AbstractFormField,
  ) {}
}
