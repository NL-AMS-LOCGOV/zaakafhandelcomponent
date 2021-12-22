/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {IdentityService} from '../../identity/identity.service';
import {Observable, of} from 'rxjs';
import {Medewerker} from '../../identity/model/medewerker';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {Groep} from '../../identity/model/groep';

@Component({
    selector: 'werkvoorraad-verdelen-dialog',
    templateUrl: 'zaken-verdelen-dialog.component.html',
    styleUrls: ['./zaken-verdelen-dialog.component.less']
})
export class ZakenVerdelenDialogComponent implements OnInit {

    medewerkerControl = new FormControl();
    groepenControl = new FormControl();
    medewerkers: Medewerker[];
    groepen: Groep[];
    filteredMedewerkers: Observable<Medewerker[]>;
    filteredGroepen: Observable<Groep[]>;
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
        this.medewerkerControl.disable();
        this.identityService.listMedewerkers().subscribe(medewerkers => {
            this.medewerkers = medewerkers;
            this.medewerkerControl.enable();
            this.filteredMedewerkers = this.medewerkerControl.valueChanges.pipe(
                startWith(''),
                map(value => (typeof value === 'string' ? value : value.naam)),
                map(name => (name ? this._filterMedewerkerNaam(name) : this.medewerkers.slice()))
            );
        });
        this.identityService.listGroepen().subscribe(groepen => {
            this.groepen = groepen;
            this.groepenControl.enable();
            this.filteredGroepen = this.groepenControl.valueChanges.pipe(
                startWith(''),
                map(value => (typeof value === 'string' ? value : value.naam)),
                map(name => (name ? this._filterGroepNaam(name) : this.groepen.slice()))
            );
        });
    }

    disableButton(): boolean {
        return this.getGroep() == null && this.getMedewerker() == null || this.loading;
    }

    private _filterMedewerkerNaam(naam: string): Medewerker[] {
        const filterValue = naam.toLowerCase();
        return this.medewerkers.filter(medewerker => medewerker.naam.toLowerCase().includes(filterValue));
    }

    private _filterGroepNaam(naam: string): Groep[] {
        const filterValue = naam.toLowerCase();
        return this.groepen.filter(groep => groep.naam.toLowerCase().includes(filterValue));
    }

    verdeel(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        this.zakenService.verdelen(this.data, this.getMedewerker(), this.getGroep()).subscribe(() => {
            this.dialogRef.close(this.medewerkerControl.value);
        });
    }

    getMedewerkerNaam(m: Medewerker): string {
        return m && m.naam ? m.naam : '';
    }

    getGroepNaam(groep: Groep): string {
        return groep && groep.naam ? groep.naam : '';
    }

    getMedewerker(): Medewerker {
        if (typeof this.medewerkerControl.value === 'object') {
            return this.medewerkerControl.value;
        } else if (typeof this.medewerkerControl.value === 'string') {
            let naam = this.medewerkerControl.value.toLowerCase();
            return this.medewerkers.find(medewerker => medewerker.naam.toLowerCase() == naam.toLowerCase());
        } else {
            return null;
        }
    }

    getGroep(): Groep {
        if (typeof this.groepenControl.value === 'object') {
            return this.groepenControl.value;
        } else if (typeof this.groepenControl.value === 'string') {
            let naam = this.groepenControl.value.toLowerCase();
            return this.groepen.find(groep => groep.naam.toLowerCase() === naam.toLowerCase());
        } else {
            return null;
        }
    }

    groepChangeEvent(event) {
        const groep: Groep = event.option.value;
        this.identityService.listMedewerkersInGroep(groep.id).subscribe(medewerkersInGroep => {
            this.filteredMedewerkers = of(medewerkersInGroep);
        });
    }
}
