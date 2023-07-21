/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { GebruikersvoorkeurenService } from "../gebruikersvoorkeuren.service";
import { Zoekopdracht } from "../model/zoekopdracht";
import { FormControl } from "@angular/forms";
import { Werklijst } from "../model/werklijst";
import { Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { UtilService } from "../../core/service/util.service";

@Component({
  templateUrl: "./zoekopdracht-save-dialog.component.html",
  styleUrls: ["./zoekopdracht-save-dialog.component.less"],
})
export class ZoekopdrachtSaveDialogComponent implements OnInit {
  loading: boolean;
  formControl = new FormControl("");
  filteredOptions: Observable<string[]>;

  constructor(
    public dialogRef: MatDialogRef<ZoekopdrachtSaveDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      zoekopdrachten: Zoekopdracht[];
      lijstID: Werklijst;
      zoekopdracht: any;
    },
    private gebruikersvoorkeurenService: GebruikersvoorkeurenService,
    private utilService: UtilService,
  ) {}

  close() {
    this.dialogRef.close();
  }

  opslaan() {
    this.dialogRef.disableClose = true;
    this.loading = true;
    let zoekopdracht;
    if (this.isNew()) {
      zoekopdracht = new Zoekopdracht();
      zoekopdracht.naam = this.formControl.value;
      zoekopdracht.json = JSON.stringify(this.data.zoekopdracht);
      zoekopdracht.lijstID = this.data.lijstID;
    } else {
      zoekopdracht = this.readZoekopdracht();
      zoekopdracht.json = JSON.stringify(this.data.zoekopdracht);
    }
    this.gebruikersvoorkeurenService
      .createOrUpdateZoekOpdrachten(zoekopdracht)
      .subscribe({
        next: () => {
          this.utilService.openSnackbar("msg.zoekopdracht.opgeslagen");
          this.dialogRef.close(true);
        },
        error: () => this.dialogRef.close(),
      });
  }

  isNew() {
    return (
      this.data.zoekopdrachten.filter(
        (value) =>
          value.naam.toLowerCase() ===
          this.formControl.value.toLowerCase().trim(),
      ).length === 0
    );
  }

  readZoekopdracht() {
    return this.data.zoekopdrachten.filter(
      (value) =>
        value.naam.toLowerCase() ===
        this.formControl.value.toLowerCase().trim(),
    )[0];
  }

  ngOnInit(): void {
    this.filteredOptions = this.formControl.valueChanges.pipe(
      startWith(""),
      map((value) => this._filter(value || "")),
    );
  }

  private _filter(name: string): string[] {
    const filterValue = name.toLowerCase();
    return this.data.zoekopdrachten
      .map((x) => x.naam)
      .filter((option) => option.toLowerCase().includes(filterValue));
  }
}
