/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {EnkelvoudigInformatieobject} from './model/enkelvoudig-informatieobject';
import {DocumentVerplaatsGegevens} from './model/document-verplaats-gegevens';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';
import {Subject} from 'rxjs';
import {ActionBarAction} from '../core/actionbar/model/action-bar-action';
import {ActionIcon} from '../shared/edit/action-icon';
import {UtilService} from '../core/service/util.service';
import {Router} from '@angular/router';
import {InformatieObjectenService} from './informatie-objecten.service';

@Injectable({
    providedIn: 'root'
})
export class InformatieObjectVerplaatsService {

    constructor(private utilService: UtilService, private router: Router, private informatieObjectService: InformatieObjectenService) {
    }

    addTeVerplaatsenDocument(informatieobject: EnkelvoudigInformatieobject, bron: string): void {
        if (!this.isReedsTeVerplaatsen(informatieobject)) {
            this._verplaatsenDocument(new DocumentVerplaatsGegevens(informatieobject.uuid, informatieobject.titel, bron));
        }
    }

    isReedsTeVerplaatsen(informatieobject: EnkelvoudigInformatieobject): boolean {
        const teVerplaatsenDocumenten = SessionStorageUtil.getItem('teVerplaatsenDocumenten', []) as DocumentVerplaatsGegevens[];
        return teVerplaatsenDocumenten.find(dvg => dvg.documentUUID === informatieobject.uuid) !== undefined;
    }

    appInit() {
        const documenten = SessionStorageUtil.getItem('teVerplaatsenDocumenten', []) as DocumentVerplaatsGegevens[];
        documenten.forEach(document => {
            this._verplaatsenDocument(document, true);
        });
    }

    private _verplaatsenDocument(document: DocumentVerplaatsGegevens, onInit?: boolean) {
        const dismiss: Subject<void> = new Subject<void>();
        dismiss.asObservable().subscribe(() => {
            this.deleteTeVerplaatsenDocument(document);
        });
        const verplaatsAction = new Subject<string>();
        verplaatsAction.asObservable().subscribe(url => {
            const nieuweZaakID = url.split('/').pop();
            this.informatieObjectService.postVerplaatsDocument(document, nieuweZaakID).subscribe(() =>
                this.utilService.openSnackbar('msg.document.verplaatsen.uitgevoerd')
            );
            this.deleteTeVerplaatsenDocument(document);
        });
        const teVerplaatsenDocumenten = SessionStorageUtil.getItem('teVerplaatsenDocumenten', []) as DocumentVerplaatsGegevens[];
        teVerplaatsenDocumenten.push(document);
        if (!onInit) {
            SessionStorageUtil.setItem('teVerplaatsenDocumenten', teVerplaatsenDocumenten);
        }
        const action: ActionBarAction = new ActionBarAction(document.documentTitel, 'document', document.bron,
            new ActionIcon('content_paste_go', verplaatsAction), dismiss, () => this.isVerplaatsenToegestaan(document));
        this.utilService.addAction(action);
    }

    private deleteTeVerplaatsenDocument(documentVerplaatsGegevens: DocumentVerplaatsGegevens) {
        const documenten = SessionStorageUtil.getItem('teVerplaatsenDocumenten', []) as DocumentVerplaatsGegevens[];
        SessionStorageUtil.setItem('teVerplaatsenDocumenten', documenten.filter(document => document.documentUUID !== documentVerplaatsGegevens.documentUUID));
    }

    private pathContains(path: string): boolean {
        return this.router.url.indexOf(path) !== -1;
    }

    private isZaakTonen(): boolean {
        return this.pathContains('zaken/ZAAK-');
    }

    private isVerplaatsenToegestaan(gegevens: DocumentVerplaatsGegevens): boolean {
        return this.isZaakTonen() && !this.pathContains(`zaken/${gegevens.bron}`);
    }
}
