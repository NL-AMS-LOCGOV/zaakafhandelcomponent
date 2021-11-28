/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {Observable} from 'rxjs';
import {Medewerker} from '../../identity/model/medewerker';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';

@Component({
    selector: 'werkvoorraad-verdelen-dialog',
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less']
})
export class ZakenVerdelenDialogComponent {

    medewerker: Medewerker;
    medewerkers: Observable<Medewerker[]>;
    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<ZakenVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[],
        private zakenService: ZakenService,
        private identityService: IdentityService) {
    }

    close(): void {
        this.dialogRef.close();
    }

    ngOnInit(): void {
        this.medewerkers = this.identityService.getMedewerkers();
    }

    setMedewerker(medewerker): void {
        this.medewerker = medewerker;
    }

    verdeel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.verdelen(this.data, this.medewerker).subscribe(() => {
            this.dialogRef.close(this.medewerker);
        });
    }
}
