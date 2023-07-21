/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, DoCheck, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { EnkelvoudigInformatieobject } from "../../../../informatie-objecten/model/enkelvoudig-informatieobject";
import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { DatumPipe } from "../../../pipes/datum.pipe";
import { InformatieObjectenService } from "../../../../informatie-objecten/informatie-objecten.service";
import { TranslateService } from "@ngx-translate/core";
import { DocumentenLijstFormField } from "./documenten-lijst-form-field";
import { Observable } from "rxjs";
import { IndicatiesLayout } from "../../../indicaties/indicaties.component";

@Component({
  templateUrl: "./documenten-lijst.component.html",
  styleUrls: ["./documenten-lijst.component.less"],
})
export class DocumentenLijstComponent
  extends FormComponent
  implements OnInit, DoCheck
{
  readonly indicatiesLayout = IndicatiesLayout;
  data: DocumentenLijstFormField;
  documenten: Observable<EnkelvoudigInformatieobject[]>;
  selection = new SelectionModel<EnkelvoudigInformatieobject>(true, []);
  dataSource: MatTableDataSource<EnkelvoudigInformatieobject> =
    new MatTableDataSource<EnkelvoudigInformatieobject>();
  datumPipe = new DatumPipe("nl");
  loading = false;

  constructor(
    public translate: TranslateService,
    public informatieObjectenService: InformatieObjectenService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.documenten = this.data.documenten;
    if (this.data.readonly && !this.data.documentenChecked) {
      this.data.removeColumn("select");
    }
    this.ophalenDocumenten();
  }

  ophalenDocumenten() {
    if (this.data.documenten) {
      this.loading = true;
      this.dataSource.data = [];
      this.data.documenten.subscribe((documenten) => {
        this.selection.clear();
        for (const document of documenten) {
          document.creatiedatum = this.datumPipe.transform(
            document.creatiedatum,
          ); // nodig voor zoeken
          document["viewLink"] = `/informatie-objecten/${document.uuid}`;
          document["downloadLink"] =
            this.informatieObjectenService.getDownloadURL(document.uuid);
          if (this.data.documentenChecked?.includes(document.uuid)) {
            this.selection.toggle(document);
          }
        }
        this.dataSource.data = documenten;
        if (this.selection.selected.length > 0) {
          this.data.formControl.setValue(
            this.selection.selected.map((value) => value.uuid).join(";"),
          );
        }
        this.loading = false;
      });
    }
  }

  updateSelected($event: MatCheckboxChange, document): void {
    if ($event) {
      this.selection.toggle(document);
      this.data.formControl.setValue(
        this.selection.selected.map((value) => value.uuid).join(";"),
      );
    }
  }

  ngDoCheck(): void {
    if (this.data.documenten !== this.documenten) {
      this.documenten = this.data.documenten;
      this.ophalenDocumenten();
    }
  }

  selectDisabled(): boolean {
    return this.data.readonly;
  }

  isSelected(document): boolean {
    return this.selection.isSelected(document);
  }
}
