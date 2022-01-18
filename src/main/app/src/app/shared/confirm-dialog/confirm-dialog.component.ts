/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ConfirmDialogData} from './confirm-dialog-data';

@Component({
    templateUrl: 'confirm-dialog.component.html',
    styleUrls: ['./confirm-dialog.component.less']
})
export class ConfirmDialogComponent implements OnInit {

    loading = false;

    constructor(
        public dialogRef: MatDialogRef<ConfirmDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
    }

    confirm(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        if (this.data.observable) {
            this.data.observable.subscribe(() => {
                this.dialogRef.close(true);
            });
        } else {
            this.dialogRef.close(true);
        }
    }
}
