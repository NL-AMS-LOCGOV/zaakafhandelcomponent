/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Notitie} from './model/notitie';
import {Medewerker} from '../../identity/model/medewerker';
import {IdentityService} from '../../identity/identity.service';
import {NotitieService} from './notities.service';

@Component({
    selector: 'zac-notities',
    templateUrl: './notities.component.html',
    styleUrls: ['./notities.component.less']
})
export class NotitiesComponent implements OnInit {

    @Input() uuid: string;
    @Input() type: string;

    @ViewChild('notitieTekst') notitieTekst;

    ingelogdeMedewerker: Medewerker;

    aantalNotities: number = 0;
    laatNotitiesSchermZien: boolean = true;
    geselecteerdeNotitieId: number;
    notities: Notitie[] = [];
    maxLengteTextArea: number = 1000;

    constructor(private identityService: IdentityService, private notitieService: NotitieService) {
    }

    ngOnInit(): void {
        this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
        this.haalNotitiesOp();
    }

    toggleNotitieContainer() {
        this.laatNotitiesSchermZien = !this.laatNotitiesSchermZien;
    }

    pasNotitieAan(id: number) {
        this.geselecteerdeNotitieId = id;

    }

    haalNotitiesOp() {
        this.notitieService.getNotities(this.type, this.uuid).subscribe(notities => {
            this.notities = notities;
            this.notities.sort((a, b) => a.tijdstipLaatsteWijziging.localeCompare(b.tijdstipLaatsteWijziging)).reverse();
            this.aantalNotities = this.notities.length;
        });
    }

    maakNotitieAan(tekst: string) {
        if (tekst.length <= this.maxLengteTextArea) {
            let notitie: Notitie = new Notitie();
            notitie.zaakUUID = this.uuid;
            notitie.tekst = tekst;
            notitie.gebruikersnaamMedewerker = this.ingelogdeMedewerker.gebruikersnaam;

            this.notitieService.createNotitie(notitie).subscribe(notitie => {
                this.notities.splice(0, 0, notitie);
                this.notitieTekst.nativeElement.value = '';
                this.aantalNotities = this.notities.length;
            });
        }
    }

    updateNotitie(notitie: Notitie, notitieTekst: string) {
        if (notitieTekst.length <= this.maxLengteTextArea) {
            notitie.tekst = notitieTekst;
            this.notitieService.updateNotitie(notitie).subscribe(() => {
                this.geselecteerdeNotitieId = null;
            });
        }
    }

    verwijderNotitie(id: number) {
        this.notitieService.deleteNotitie(id).subscribe(() => {
            this.notities.splice(this.notities.findIndex(n => n.id === id), 1);
            this.aantalNotities = this.notities.length;
        });
    }

}
