/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { TakenService } from "../taken.service";
import { MaterialFormBuilderService } from "../../shared/material-form-builder/material-form-builder.service";
import { TaakZoekObject } from "../../zoeken/model/taken/taak-zoek-object";
import { MedewerkerGroepFormField } from "../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-form-field";
import { MedewerkerGroepFieldBuilder } from "../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-field-builder";
import { Group } from "../../identity/model/group";
import { User } from "../../identity/model/user";
import { InputFormField } from "../../shared/material-form-builder/form-components/input/input-form-field";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";

@Component({
  selector: "zac-taken-verdelen-dialog",
  templateUrl: "./taken-verdelen-dialog.component.html",
  styleUrls: ["./taken-verdelen-dialog.component.less"],
})
export class TakenVerdelenDialogComponent implements OnInit {
  medewerkerGroepFormField: MedewerkerGroepFormField;
  redenFormField: InputFormField;
  loading = false;

  constructor(
    public dialogRef: MatDialogRef<TakenVerdelenDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaakZoekObject[],
    private mfbService: MaterialFormBuilderService,
    private takenService: TakenService,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
    this.medewerkerGroepFormField = new MedewerkerGroepFieldBuilder()
      .id("toekenning")
      .groepLabel("actie.taak.toekennen.groep")
      .medewerkerLabel("actie.taak.toekennen.medewerker")
      .maxlength(50)
      .build();
    this.redenFormField = new InputFormFieldBuilder()
      .id("reden")
      .label("reden")
      .maxlength(100)
      .build();
  }

  isDisabled(): boolean {
    return (
      (!this.medewerkerGroepFormField.medewerker.value &&
        !this.medewerkerGroepFormField.groep.value) ||
      this.medewerkerGroepFormField.formControl.invalid ||
      this.loading
    );
  }

  verdeel(): void {
    const toekenning: { groep?: Group; medewerker?: User } =
      this.medewerkerGroepFormField.formControl.value;
    const reden: string = this.redenFormField.formControl.value;
    this.dialogRef.disableClose = true;
    this.loading = true;
    this.takenService
      .verdelenVanuitLijst(
        this.data,
        reden,
        toekenning.groep,
        toekenning.medewerker,
      )
      .subscribe(() => {
        this.dialogRef.close(toekenning.groep || toekenning.medewerker);
      });
  }
}
