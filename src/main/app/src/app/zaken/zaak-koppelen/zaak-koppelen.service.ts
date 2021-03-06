/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Zaak} from '../model/zaak';
import {Subject} from 'rxjs';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {ActionBarAction} from '../../core/actionbar/model/action-bar-action';
import {ActionIcon} from '../../shared/edit/action-icon';
import {ZaakKoppelGegevens} from '../model/zaak-koppel-gegevens';
import {ZaakKoppelenDialogComponent} from './zaak-koppelen-dialog.component';
import {UtilService} from '../../core/service/util.service';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {PaginaLocatieUtil} from '../../locatie/pagina-locatie.util';

@Injectable({
    providedIn: 'root'
})
export class ZaakKoppelenService {

    constructor(private utilService: UtilService, private router: Router, private dialog: MatDialog) {
    }

    addTeKoppelenZaak(zaak: Zaak): void {
        if (!this.isReedsTeKoppelen(zaak)) {
            this._koppelenZaak(zaak);
        }
    }

    isReedsTeKoppelen(zaak: Zaak): boolean {
        const teKoppelenZaken = SessionStorageUtil.getItem('teKoppelenZaken', []) as Zaak[];
        return teKoppelenZaken.find(_zaak => _zaak.uuid === zaak.uuid) !== undefined;
    }

    appInit() {
        const zaken = SessionStorageUtil.getItem('teKoppelenZaken', []) as Zaak[];
        zaken.forEach(zaak => {
            this._koppelenZaak(zaak, true);
        });
    }

    private _koppelenZaak(zaak: Zaak, onInit?: boolean) {
        const dismiss: Subject<void> = new Subject<void>();
        dismiss.asObservable().subscribe(() => {
            this.deleteTeKoppelenZaak(zaak);
        });
        const editAction = new Subject<string>();
        editAction.asObservable().subscribe(url => {
            const nieuwZaakID = url.split('/').pop();
            this.openDialog(zaak, nieuwZaakID);
        });
        const teKoppelenZaken = SessionStorageUtil.getItem('teKoppelenZaken', []) as Zaak[];
        teKoppelenZaken.push(zaak);
        if (!onInit) {
            SessionStorageUtil.setItem('teKoppelenZaken', teKoppelenZaken);
        }
        const action: ActionBarAction = new ActionBarAction(zaak.identificatie, 'zaak', zaak.identificatie,
            new ActionIcon('link', editAction), dismiss, () => this.isKoppelenToegestaan(zaak.identificatie));
        this.utilService.addAction(action);
    }

    private openDialog(zaak: Zaak, nieuwZaakID: string) {
        const zaakKoppelGegevens = new ZaakKoppelGegevens();
        zaakKoppelGegevens.bronZaakUuid = zaak.uuid;
        zaakKoppelGegevens.identificatie = nieuwZaakID;

        this.dialog.open(ZaakKoppelenDialogComponent, {
            data: zaakKoppelGegevens,
        });
    }

    private deleteTeKoppelenZaak(zaak: Zaak) {
        const zaken = SessionStorageUtil.getItem('teKoppelenZaken', []) as Zaak[];
        SessionStorageUtil.setItem('teKoppelenZaken', zaken.filter(_zaak => _zaak.uuid !== zaak.uuid));
    }

    private isKoppelenToegestaan(zaakIdentificatie: string): boolean {
        return PaginaLocatieUtil.actieveZaakViewIdentificatie && PaginaLocatieUtil.actieveZaakViewIdentificatie !== zaakIdentificatie;
    }
}
