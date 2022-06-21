/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Observable} from 'rxjs';

@Component({
    templateUrl: 'confirm-dialog.component.html',
    styleUrls: ['./confirm-dialog.component.less']
})

export class ConfirmDialogComponent {
    loading = false;

    constructor(
        public dialogRef: MatDialogRef<ConfirmDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData) {
    }

    confirm(): void {
        if (this.data.observable) {
            this.loading = true;
            this.dialogRef.disableClose = true;
            this.data.observable.subscribe(() => {
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

export class ConfirmDialogData {
    constructor(public melding: string, public observable?: Observable<any>, public uitleg?: string) {}
}
