/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';

@Component({
    selector: 'werkvoorraad-vrijgeven-dialog',
    templateUrl: 'zaken-vrijgeven-dialog.component.html',
    styleUrls: ['./zaken-vrijgeven-dialog.component.less']
})
export class ZakenVrijgevenDialogComponent {

    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<ZakenVrijgevenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[],
        private zakenService: ZakenService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    vrijgeven(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.vrijgeven(this.data).subscribe(() => {
            this.dialogRef.close(true);
        });
    }
}
