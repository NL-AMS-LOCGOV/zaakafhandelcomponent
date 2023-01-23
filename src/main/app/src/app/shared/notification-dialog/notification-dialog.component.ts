/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';

@Component({
    templateUrl: './notification-dialog.component.html'
})
export class NotificationDialogComponent {

    constructor(
        public dialogRef: MatDialogRef<NotificationDialogData>,
        @Inject(MAT_DIALOG_DATA) public data: NotificationDialogData) {
    }

    confirm(): void {
        this.dialogRef.close(true);
    }
}

export class NotificationDialogData {
    constructor(public melding: string) {}
}
