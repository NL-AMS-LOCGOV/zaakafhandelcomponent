/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit, ViewChild } from "@angular/core";
import { UtilService } from "../../core/service/util.service";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { IdentityService } from "../../identity/identity.service";
import { AdminComponent } from "../admin/admin.component";
import { ReferentieTabelService } from "../referentie-tabel.service";
import { ReferentieTabel } from "../model/referentie-tabel";
import { Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { InputFormField } from "../../shared/material-form-builder/form-components/input/input-form-field";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { MatTableDataSource } from "@angular/material/table";
import { ReferentieTabelWaarde } from "../model/referentie-tabel-waarde";
import { Observable, of } from "rxjs";
import { FoutAfhandelingService } from "../../fout-afhandeling/fout-afhandeling.service";
import { catchError } from "rxjs/operators";
import { CdkDragDrop, moveItemInArray } from "@angular/cdk/drag-drop";

@Component({
  templateUrl: "./referentie-tabel.component.html",
  styleUrls: ["./referentie-tabel.component.less"],
})
export class ReferentieTabelComponent extends AdminComponent implements OnInit {
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  tabel: ReferentieTabel;

  codeFormField: InputFormField;
  naamFormField: InputFormField;

  isLoadingResults = false;
  columns: string[] = ["naam", "id"];
  dataSource: MatTableDataSource<ReferentieTabelWaarde> =
    new MatTableDataSource<ReferentieTabelWaarde>();

  waardeFormField: InputFormField[] = [];

  constructor(
    private identityService: IdentityService,
    private service: ReferentieTabelService,
    public utilService: UtilService,
    private route: ActivatedRoute,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      this.init(data.tabel);
    });
  }

  init(tabel: ReferentieTabel): void {
    this.tabel = tabel;
    if (this.tabel.waarden == null) {
      this.tabel.waarden = [];
    }
    this.setupMenu("title.referentietabel", { tabel: this.tabel.code });
    this.createForm();
    this.laadTabelWaarden();
  }

  createForm() {
    this.codeFormField = new InputFormFieldBuilder(this.tabel.code)
      .id("code")
      .label("tabel")
      .validators(Validators.required)
      .build();
    this.naamFormField = new InputFormFieldBuilder(this.tabel.naam)
      .id("naam")
      .label("naam")
      .validators(Validators.required)
      .build();
  }

  editTabel(event: any, field: string): void {
    this.tabel[field] = event[field];
    this.persistTabel();
  }

  laadTabelWaarden(): void {
    this.isLoadingResults = true;
    this.tabel.waarden.forEach((waarde) => {
      this.waardeFormField[waarde.id] = new InputFormFieldBuilder(waarde.naam)
        .id("waarde_" + waarde.id)
        .label("waarde")
        .validators(Validators.required)
        .build();
    });
    this.dataSource.data = this.tabel.waarden;
    this.isLoadingResults = false;
  }

  nieuweTabelWaarde() {
    const waarde: ReferentieTabelWaarde = new ReferentieTabelWaarde();
    waarde.naam = this.getUniqueNaam(1);
    this.tabel.waarden.push(waarde);
    this.persistTabel();
  }

  editTabelWaarde(event: any, row: ReferentieTabelWaarde): void {
    const naam: string = event["waarde_" + row.id];
    for (const waarde of this.tabel.waarden) {
      if (waarde.naam === naam) {
        this.foutAfhandelingService.openFoutDialog(
          'Deze referentietabel bevat al een "' + naam + '" waarde.',
        );
        return;
      }
    }
    this.tabel.waarden[this.getTabelWaardeIndex(row)].naam = naam;
    this.persistTabel();
  }

  moveTabelWaarde(event: CdkDragDrop<ReferentieTabelWaarde[]>) {
    const sameRow = event.previousIndex === event.currentIndex;
    if (!sameRow) {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
      this.persistTabel();
    }
  }

  verwijderTabelWaarde(row: ReferentieTabelWaarde): void {
    this.tabel.waarden.splice(this.getTabelWaardeIndex(row), 1);
    this.persistTabel();
  }

  private getTabelWaardeIndex(row: ReferentieTabelWaarde) {
    return this.tabel.waarden.findIndex((waarde) => waarde.id === row.id);
  }

  private getUniqueNaam(i: number): string {
    let naam: string = "Nieuwe waarde" + (1 < i ? " " + i : "");
    this.tabel.waarden.forEach((waarde) => {
      if (waarde.naam === naam) {
        naam = this.getUniqueNaam(i + 1);
        return;
      }
    });
    return naam;
  }

  private persistTabel(): void {
    const persistReferentieTabel: Observable<ReferentieTabel> =
      this.tabel.id != null
        ? this.service.updateReferentieTabel(this.tabel)
        : this.service.createReferentieTabel(this.tabel);
    persistReferentieTabel
      .pipe(catchError(() => of(this.tabel)))
      .subscribe((persistedTabel) => {
        this.init(persistedTabel);
      });
  }
}
