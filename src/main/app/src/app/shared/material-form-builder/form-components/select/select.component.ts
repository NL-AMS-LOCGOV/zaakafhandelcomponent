/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { SelectFormField } from "./select-form-field";
import { TranslateService } from "@ngx-translate/core";
import { Subscription } from "rxjs";

@Component({
  templateUrl: "./select.component.html",
  styleUrls: ["./select.component.less"],
})
export class SelectComponent
  extends FormComponent
  implements OnInit, OnDestroy
{
  data: SelectFormField;
  loading$: Subscription;

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {
    this.loading$ = this.data.loading$.subscribe((loading) => {
      loading
        ? this.data.formControl.disable()
        : this.data.formControl.enable();
    });
  }

  ngOnDestroy(): void {
    this.loading$.unsubscribe();
  }
}
