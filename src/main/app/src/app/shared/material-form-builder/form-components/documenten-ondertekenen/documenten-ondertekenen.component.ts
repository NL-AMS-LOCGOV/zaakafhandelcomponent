/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, DoCheck, OnInit } from "@angular/core";
import { InformatieObjectenService } from "../../../../informatie-objecten/informatie-objecten.service";
import { TranslateService } from "@ngx-translate/core";
import { DocumentenLijstComponent } from "../documenten-lijst/documenten-lijst.component";
import { DocumentenOndertekenenFormField } from "./documenten-ondertekenen-form-field";

@Component({
  templateUrl: "../documenten-lijst/documenten-lijst.component.html",
  styleUrls: ["../documenten-lijst/documenten-lijst.component.less"],
})
export class DocumentenOndertekenenComponent
  extends DocumentenLijstComponent
  implements OnInit, DoCheck
{
  data: DocumentenOndertekenenFormField;

  constructor(
    public translate: TranslateService,
    public informatieObjectenService: InformatieObjectenService,
  ) {
    super(translate, informatieObjectenService);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngDoCheck(): void {
    super.ngDoCheck();
  }

  toonFilterVeld(): boolean {
    return false;
  }

  selectDisabled(document): boolean {
    return (
      this.data.readonly ||
      !document.rechten.ondertekenen ||
      document.ondertekening
    );
  }

  isSelected(document): boolean {
    return this.selection.isSelected(document) || document.ondertekening;
  }
}
