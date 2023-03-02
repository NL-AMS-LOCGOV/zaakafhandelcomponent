/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {TabelGegevens} from '../model/tabel-gegevens';
import {GebruikersvoorkeurenService} from '../../../gebruikersvoorkeuren/gebruikersvoorkeuren.service';
import {Werklijst} from '../../../gebruikersvoorkeuren/model/werklijst';
import {ZakenWerkvoorraadComponent} from '../../../zaken/zaken-werkvoorraad/zaken-werkvoorraad.component';

@Injectable({
    providedIn: 'root'
})
export class TabelGegevensResolver implements Resolve<TabelGegevens> {
    constructor(private gebruikersvoorkeurenService: GebruikersvoorkeurenService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<TabelGegevens> {
        return this.gebruikersvoorkeurenService.readTabelGegevens(this.getWerklijst(route.component.name));
    }

    getWerklijst(component: string): Werklijst {
        switch (component) {
            case 'ZakenWerkvoorraadComponent':
                return Werklijst.WERKVOORRAAD_ZAKEN;
            case 'ZakenMijnComponent':
                return Werklijst.MIJN_ZAKEN;
            case 'ZakenAfgehandeldComponent':
                return Werklijst.AFGEHANDELDE_ZAKEN;
            case 'TakenWerkvoorraadComponent':
                return Werklijst.WERKVOORRAAD_TAKEN;
            case 'TakenMijnComponent':
                return Werklijst.MIJN_TAKEN;
            case 'OntkoppeldeDocumentenListComponent':
                return Werklijst.ONTKOPPELDE_DOCUMENTEN;
            case 'InboxDocumentenListComponent':
                return Werklijst.INBOX_DOCUMENTEN;
            default:
                throw Error('Geen werklijst gevonden voor ' + component);
        }
    }

}
