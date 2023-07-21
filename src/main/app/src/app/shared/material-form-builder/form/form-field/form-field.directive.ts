/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Directive, ViewContainerRef } from "@angular/core";

@Directive({
  selector: "[mfb-form-field]",
})
export class FormFieldDirective {
  constructor(public viewContainerRef: ViewContainerRef) {}
}
