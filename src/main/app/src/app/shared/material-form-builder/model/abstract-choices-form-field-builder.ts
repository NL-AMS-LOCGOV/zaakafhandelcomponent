/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "./abstract-form-field-builder";
import { AbstractChoicesFormField } from "./abstract-choices-form-field";
import { isObservable, Observable, of as observableOf } from "rxjs";

export abstract class AbstractChoicesFormFieldBuilder extends AbstractFormFieldBuilder {
  abstract readonly formField: AbstractChoicesFormField;

  constructor() {
    super();
  }

  optionLabel(optionLabel: string): this {
    this.formField.optionLabel = optionLabel;
    return this;
  }

  optionSuffix(optionSuffix: string): this {
    this.formField.optionSuffix = optionSuffix;
    return this;
  }

  optionValue(optionValue: string): this {
    this.formField.optionValue = optionValue;
    return this;
  }

  optionsOrder(optionOrderFn: (a: any, b: any) => number): this {
    this.formField.optionOrderFn = optionOrderFn;
    return this;
  }

  options(options: Observable<any[]> | any[]): this {
    if (isObservable(options)) {
      this.formField.options = options;
    } else {
      this.formField.options = observableOf(options);
    }
    return this;
  }
}
