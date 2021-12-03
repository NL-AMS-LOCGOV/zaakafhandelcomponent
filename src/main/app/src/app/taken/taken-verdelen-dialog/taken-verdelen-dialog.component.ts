/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {Medewerker} from '../../identity/model/medewerker';
import {Observable} from 'rxjs';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {Taak} from '../model/taak';
import {TakenService} from '../taken.service';

@Component({
    selector: 'zac-taken-verdelen-dialog',
    templateUrl: './taken-verdelen-dialog.component.html',
    styleUrls: ['./taken-verdelen-dialog.component.less']
})
export class TakenVerdelenDialogComponent implements OnInit {

    medewerker: Medewerker;
    medewerkers: Observable<Medewerker[]>;
    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<TakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: Taak[],
        private takenService: TakenService,
        private identityService: IdentityService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.medewerkers = this.identityService.listMedewerkers();
    }

    setMedewerker(medewerker): void {
        this.medewerker = medewerker;
    }

    verdeel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.takenService.verdelen(this.data, this.medewerker).subscribe(() => {
            this.dialogRef.close(this.medewerker);
        });
    }

}
