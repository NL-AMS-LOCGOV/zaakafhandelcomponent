/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';

@Component({
    templateUrl: 'fout-dialog.component.html'
})
export class FoutDialogComponent {

    constructor(public dialogRef: MatDialogRef<FoutDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: string) {
    }

    close(): void {
        this.dialogRef.close();
    }
}
