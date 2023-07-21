/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { TakenService } from "../taken.service";
import { TaakZoekObject } from "../../zoeken/model/taken/taak-zoek-object";
import { InputFormField } from "../../shared/material-form-builder/form-components/input/input-form-field";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";

@Component({
  selector: "zac-taken-vrijgeven-dialog",
  templateUrl: "./taken-vrijgeven-dialog.component.html",
  styleUrls: ["./taken-vrijgeven-dialog.component.less"],
})
export class TakenVrijgevenDialogComponent implements OnInit {
  loading: boolean;
  redenFormField: InputFormField;

  constructor(
    public dialogRef: MatDialogRef<TakenVrijgevenDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaakZoekObject[],
    private takenService: TakenService,
  ) {}

  ngOnInit(): void {
    this.redenFormField = new InputFormFieldBuilder()
      .id("reden")
      .label("reden")
      .maxlength(100)
      .build();
  }

  close() {
    this.dialogRef.close();
  }

  vrijgeven() {
    this.dialogRef.disableClose = true;
    this.loading = true;
    const reden: string = this.redenFormField.formControl.value;
    this.takenService.vrijgevenVanuitLijst(this.data, reden).subscribe(() => {
      this.dialogRef.close(true);
    });
  }
}
