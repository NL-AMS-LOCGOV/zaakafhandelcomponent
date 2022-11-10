/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TakenService} from '../taken.service';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';

@Component({
    selector: 'zac-taken-vrijgeven-dialog',
    templateUrl: './taken-vrijgeven-dialog.component.html',
    styleUrls: ['./taken-vrijgeven-dialog.component.less']
})
export class TakenVrijgevenDialogComponent {

    loading: boolean;

    constructor(public dialogRef: MatDialogRef<TakenVrijgevenDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: TaakZoekObject[],
                private takenService: TakenService) { }

    close() {
        this.dialogRef.close();
    }

    vrijgeven() {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.takenService.vrijgevenVanuitLijst(this.data).subscribe(() => {
            this.dialogRef.close(true);
        });
    }

}
