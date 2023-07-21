/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ZakenService } from "../zaken.service";
import { AbstractFormField } from "../../shared/material-form-builder/model/abstract-form-field";
import { TextareaFormFieldBuilder } from "../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { ZaakOntkoppelGegevens } from "../model/zaak-ontkoppel-gegevens";
import { Validators } from "@angular/forms";

@Component({
  templateUrl: "zaak-ontkoppelen-dialog.component.html",
})
export class ZaakOntkoppelenDialogComponent implements OnInit {
  redenFormField: AbstractFormField;
  loading: boolean;

  constructor(
    public dialogRef: MatDialogRef<ZaakOntkoppelenDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ZaakOntkoppelGegevens,
    private zakenService: ZakenService,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
    this.redenFormField = new TextareaFormFieldBuilder()
      .id("reden")
      .label("reden")
      .maxlength(100)
      .validators(Validators.required)
      .build();
  }

  ontkoppel(): void {
    this.dialogRef.disableClose = true;
    this.loading = true;
    this.data.reden = this.redenFormField.formControl.value;
    this.zakenService.ontkoppelZaak(this.data).subscribe(() => {
      this.dialogRef.close(true);
    });
  }
}
