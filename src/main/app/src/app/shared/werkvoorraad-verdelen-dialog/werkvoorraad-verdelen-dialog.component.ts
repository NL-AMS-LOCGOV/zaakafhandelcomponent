/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {Observable} from 'rxjs';
import {Medewerker} from '../../identity/model/medewerker';
import {ZakenService} from '../../zaken/zaken.service';
import {TakenService} from '../../taken/taken.service';

@Component({
    selector: 'werkvoorraad-verdelen-dialog',
    templateUrl: 'werkvoorraad-verdelen-dialog.component.html',
    styleUrls: ['./werkvoorraad-verdelen-dialog.component.less']
})
export class WerkvoorraadVerdelenDialogComponent {

    medewerker: Medewerker;
    medewerkers: Observable<Medewerker[]>;
    loading: boolean;

    constructor(
        public dialogRef: MatDialogRef<WerkvoorraadVerdelenDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any[],
        private zakenService: ZakenService,
        private takenService: TakenService,
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
        if (this.bepaalVerdeelZaken()) {
            this.zakenService.verdelen(this.data, this.medewerker).subscribe(() => {
                this.dialogRef.close(this.medewerker);
            });
        } else {
            this.takenService.verdelen(this.data, this.medewerker).subscribe(() => {
                this.dialogRef.close(this.medewerker);
            });
        }
    }

    /**
     * Check whether the first item in the array does not contain zaakUUID. If true, array contains objects of type Zaak
     */
    bepaalVerdeelZaken(): boolean {
        return !this.data[0]['zaakUUID'];
    }
}
