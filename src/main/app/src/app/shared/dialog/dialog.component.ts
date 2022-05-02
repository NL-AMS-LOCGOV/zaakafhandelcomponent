/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DialogData} from './dialog-data';

@Component({
    templateUrl: 'dialog.component.html',
    styleUrls: ['./dialog.component.less']
})
export class DialogComponent {

    loading = false;

    constructor(
        public dialogRef: MatDialogRef<DialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    }

    confirm(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        if (this.data.fn) {
            const results: any[] = [];
            for (const formField of this.data.formFields) {
                results[formField.id] = formField.formControl.value;
            }
            this.data.fn(results).subscribe(() => {
                this.dialogRef.close(true);
            });
        } else {
            this.dialogRef.close(true);
        }
    }

    cancel(): void {
        this.dialogRef.close(false);
    }
}
