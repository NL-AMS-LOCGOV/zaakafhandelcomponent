/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {IdentityService} from '../../identity/identity.service';
import {Observable} from 'rxjs';
import {Medewerker} from '../../identity/model/medewerker';

@Component({
    selector: 'zaken-verdelen-dialog',
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less'],
})
export class ZakenVerdelenDialogComponent {

    medewerker: Medewerker;
    medewerkers: Observable<Medewerker[]>

    constructor(
        public dialogRef: MatDialogRef<ZakenVerdelenDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: ZaakOverzicht[], private identityService: IdentityService) {
        console.log(data);
    }

    onNoClick(): void {
        this.dialogRef.close();
    }


    ngOnInit(): void {
        this.medewerkers = this.identityService.getMedewerkers();
    }

    setMedewerker(medewerker): void {
        this.medewerker = medewerker;
    }
}
