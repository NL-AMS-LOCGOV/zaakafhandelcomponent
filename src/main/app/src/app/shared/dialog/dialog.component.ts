/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { DialogData } from "./dialog-data";
import { FieldType } from "../material-form-builder/model/field-type.enum";

@Component({
  templateUrl: "dialog.component.html",
  styleUrls: ["./dialog.component.less"],
})
export class DialogComponent implements OnInit {
  loading = true;

  constructor(
    public dialogRef: MatDialogRef<DialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {}

  ngOnInit(): void {
    this.dialogRef.afterOpened().subscribe(() => {
      this.loading = false;
    });
  }

  confirm(): void {
    this.dialogRef.disableClose = true;
    this.loading = true;
    if (this.data.fn) {
      const results: any[] = [];
      for (const formField of this.data.formFields) {
        switch (formField.fieldType) {
          case FieldType.CHECKBOX:
            results[formField.id] =
              formField.formControl.value != null &&
              formField.formControl.value;
            break;
          default:
            results[formField.id] = formField.formControl.value;
            break;
        }
      }
      this.data.fn(results).subscribe({
        next: () => this.dialogRef.close(true),
        error: () => this.dialogRef.close(false),
      });
    } else {
      this.dialogRef.close(true);
    }
  }

  cancel(): void {
    this.dialogRef.close(false);
  }

  disabled() {
    return this.loading || (this.data && this.data.formFieldsInvalid());
  }
}
