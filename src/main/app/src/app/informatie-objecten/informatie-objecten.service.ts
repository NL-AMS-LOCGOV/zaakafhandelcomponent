/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable, Subject} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {EnkelvoudigInformatieobject} from './model/enkelvoudig-informatieobject';
import {ZaakInformatieobject} from './model/zaak-informatieobject';
import {Informatieobjecttype} from './model/informatieobjecttype';
import {HistorieRegel} from '../shared/historie/model/historie-regel';
import {EnkelvoudigInformatieObjectZoekParameters} from './model/enkelvoudig-informatie-object-zoek-parameters';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';
import {UtilService} from '../core/service/util.service';
import {Router} from '@angular/router';
import {ActionIcon} from '../shared/edit/action-icon';
import {DocumentVerplaatsGegevens} from './model/document-verplaats-gegevens';
import {ActionBarAction} from '../core/actionbar/model/action-bar-action';
import {EnkelvoudigInformatieObjectVersieGegevens} from './model/enkelvoudig-informatie-object-versie-gegevens';
import {DocumentCreatieGegevens} from './model/document-creatie-gegevens';

@Injectable({
    providedIn: 'root'
})
export class InformatieObjectenService {

    private basepath = '/rest/informatieobjecten';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService, private utilService: UtilService, private router: Router) {
    }

    readEnkelvoudigInformatieobject(uuid: string): Observable<EnkelvoudigInformatieobject> {
        return this.http.get<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readEnkelvoudigInformatieobjectVersie(uuid: string, versie: number): Observable<EnkelvoudigInformatieobject> {
        return this.http.get<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/versie/${uuid}/${versie}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listInformatieobjecttypes(zaakTypeID): Observable<Informatieobjecttype[]> {
        return this.http.get<Informatieobjecttype[]>(`${this.basepath}/informatieobjecttypes/${zaakTypeID}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listInformatieobjecttypesForZaak(zaakUUID): Observable<Informatieobjecttype[]> {
        return this.http.get<Informatieobjecttype[]>(`${this.basepath}/informatieobjecttypes/zaak/${zaakUUID}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createEnkelvoudigInformatieobject(zaakUuid: string, documentReferentieId: string,
                                      infoObject: EnkelvoudigInformatieobject,
                                      taakObject: boolean): Observable<EnkelvoudigInformatieobject> {
        return this.http.post<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/${zaakUuid}/${documentReferentieId}`, infoObject, {
            params: {
                taakObject: taakObject
            }
        }).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createDocument(documentCreatieGegevens: DocumentCreatieGegevens): Observable<string> {
        return this.http.post<string>(`${this.basepath}/documentcreatie`, documentCreatieGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readHuidigeVersieEnkelvoudigInformatieObject(uuid: string): Observable<EnkelvoudigInformatieObjectVersieGegevens> {
        return this.http.get<EnkelvoudigInformatieObjectVersieGegevens>(`${this.basepath}/informatieobject/${uuid}/huidigeversie`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    partialUpdateEnkelvoudigInformatieobject(documentNieuweVersieGegevens: EnkelvoudigInformatieObjectVersieGegevens): Observable<EnkelvoudigInformatieobject> {
        return this.http.post<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/partialupdate`, documentNieuweVersieGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listEnkelvoudigInformatieobjecten(zoekParameters: EnkelvoudigInformatieObjectZoekParameters): Observable<EnkelvoudigInformatieobject[]> {
        return this.http.put<EnkelvoudigInformatieobject[]>(`${this.basepath}/informatieobjectenList`, zoekParameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakInformatieobjecten(uuid: string): Observable<ZaakInformatieobject[]> {
        return this.http.get<ZaakInformatieobject[]>(`${this.basepath}/informatieobject/${uuid}/zaken`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listHistorie(uuid: string): Observable<HistorieRegel[]> {
        return this.http.get<HistorieRegel[]>(`${this.basepath}/informatieobject/${uuid}/historie`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    lockInformatieObject(uuid: string) {
        return this.http.post<void>(`${this.basepath}/informatieobject/${uuid}/lock`, null).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    unlockInformatieObject(uuid: string) {
        return this.http.post<void>(`${this.basepath}/informatieobject/${uuid}/unlock`, null).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getDownloadURL(uuid: string, versie?: number): string {
        if (versie) {
            return `${this.basepath}/informatieobject/${uuid}/${versie}/download`;
        }
        return `${this.basepath}/informatieobject/${uuid}/download`;
    }

    getUploadURL(uuid: string): string {
        return `${this.basepath}/informatieobject/upload/${uuid}`;
    }

    getPreviewDocument(uuid: string, versie?: number) {
        let url = `${this.basepath}/informatieobject/${uuid}/download`;
        if (versie) {
            url = `${this.basepath}/informatieobject/${uuid}/${versie}/download`;
        }

        return this.http.get(url, {responseType: 'blob'}).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    postVerplaatsDocument(documentVerplaatsGegevens: DocumentVerplaatsGegevens, nieuweZaakID: string): Observable<void> {
        documentVerplaatsGegevens.nieuweZaakID = nieuweZaakID;
        return this.http.post<void>(`${this.basepath}/informatieobject/verplaats`, documentVerplaatsGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
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
            this.postVerplaatsDocument(document, nieuweZaakID).subscribe(() =>
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
