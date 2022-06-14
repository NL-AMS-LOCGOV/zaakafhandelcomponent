/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Zaak} from './model/zaak';
import {Observable} from 'rxjs';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {TableRequest} from '../shared/dynamic-table/datasource/table-request';
import {Zaaktype} from './model/zaaktype';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {ZaakOverzicht} from './model/zaak-overzicht';
import {ZaakToekennenGegevens} from './model/zaak-toekennen-gegevens';
import {User} from '../identity/model/user';
import {ZakenVerdeelGegevens} from './model/zaken-verdeel-gegevens';
import {HistorieRegel} from '../shared/historie/model/historie-regel';
import {Group} from '../identity/model/group';
import {ZaakEditMetRedenGegevens} from './model/zaak-edit-met-reden-gegevens';
import {ZaakBetrokkeneGegevens} from './model/zaak-betrokkene-gegevens';
import {ZaakbeeindigReden} from '../admin/model/zaakbeeindig-reden';
import {ZaakAfbrekenGegevens} from './model/zaak-afbreken-gegevens';
import {DocumentOntkoppelGegevens} from './model/document-ontkoppel-gegevens';
import {ZaakOpschortGegevens} from './model/zaak-opschort-gegevens';
import {ZaakOpschorting} from './model/zaak-opschorting';
import {ZaakVerlengGegevens} from './model/zaak-verleng-gegevens';
import {ZaakZoekObject} from '../zoeken/model/zaken/zaak-zoek-object';

@Injectable({
    providedIn: 'root'
})
export class ZakenService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/zaken';

    private static getTableParams(request: TableRequest): HttpParams {
        return new HttpParams().set('tableRequest', JSON.stringify(request));
    }

    readZaak(uuid: string): Observable<Zaak> {
        return this.http.get<Zaak>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(this.handleError)
        );
    }

    readZaakByID(id: string): Observable<Zaak> {
        return this.http.get<Zaak>(`${this.basepath}/zaak/id/${id}`).pipe(
            catchError(this.handleError)
        );
    }

    createZaak(zaak: Zaak): Observable<Zaak> {
        return this.http.post<Zaak>(`${this.basepath}/zaak`, zaak).pipe(
            catchError(this.handleError)
        );
    }

    partialUpdateZaak(uuid: string, zaak: Zaak, reden?: string): Observable<Zaak> {
        const zaakEditMetRedenGegevens: ZaakEditMetRedenGegevens = new ZaakEditMetRedenGegevens();
        zaakEditMetRedenGegevens.zaak = zaak;
        zaakEditMetRedenGegevens.reden = reden;
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${uuid}`, zaakEditMetRedenGegevens).pipe(
            catchError(this.handleError)
        );
    }

    readOpschortingZaak(uuid: string): Observable<ZaakOpschorting> {
        return this.http.get<ZaakOpschorting>(`${this.basepath}/zaak/${uuid}/opschorting`).pipe(
            catchError(this.handleError)
        );
    }

    opschortenZaak(uuid: string, zaakOpschortGegevens: ZaakOpschortGegevens): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${uuid}/opschorting`, zaakOpschortGegevens).pipe(
            catchError(this.handleError)
        );
    }

    verlengenZaak(zaakUUID: string, zaakVerlengGegevens: ZaakVerlengGegevens): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${zaakUUID}/verlenging`, zaakVerlengGegevens).pipe(
            catchError(this.handleError)
        );
    }


    listZaakWaarschuwingen(): Observable<ZaakOverzicht[]> {
        return this.http.get<ZaakOverzicht[]>(`${this.basepath}/waarschuwing`).pipe(
            catchError(this.handleError)
        );
    }

    listZaaktypes(): Observable<Zaaktype[]> {
        return this.http.get<Zaaktype[]>(`${this.basepath}/zaaktypes`).pipe(
            catchError(this.handleError)
        );
    }

    toekennen(zaak: Zaak, reden?: string): Observable<Zaak> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.behandelaarGebruikersnaam = zaak.behandelaar?.id;
        toekennenGegevens.reden = reden;

        return this.http.put<Zaak>(`${this.basepath}/toekennen`, toekennenGegevens).pipe(
            catchError(this.handleError)
        );
    }

    toekennenGroep(zaak: Zaak, reden?: string): Observable<Zaak> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.groepId = zaak.groep?.id;
        toekennenGegevens.reden = reden;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/groep`, toekennenGegevens).pipe(
            catchError(this.handleError)
        );
    }

    verdelen(zaken: ZaakOverzicht[], groep?: Group, medewerker?: User, reden?: string): Observable<void> {
        const verdeelGegevens: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
        verdeelGegevens.uuids = zaken.map(zaak => zaak.uuid);
        verdeelGegevens.groepId = groep?.id;
        verdeelGegevens.behandelaarGebruikersnaam = medewerker?.id;
        verdeelGegevens.reden = reden;

        return this.http.put<void>(`${this.basepath}/verdelen`, verdeelGegevens).pipe(
            catchError(this.handleError)
        );
    }

    vrijgeven(zaken: ZaakOverzicht[] | Zaak[], reden?: string): Observable<void> {
        const verdeelGegevens: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
        verdeelGegevens.uuids = zaken.map(zaak => zaak.uuid);
        verdeelGegevens.reden = reden;

        return this.http.put<void>(`${this.basepath}/vrijgeven`, verdeelGegevens).pipe(
            catchError(this.handleError)
        );
    }

    toekennenAanIngelogdeMedewerker(zaak: Zaak, reden?: string): Observable<Zaak> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.reden = reden;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/mij`, toekennenGegevens).pipe(
            catchError(this.handleError)
        );
    }

    createInitiator(zaak: Zaak, betrokkeneIdentificatie: string): Observable<void> {
        const gegevens = new ZaakBetrokkeneGegevens();
        gegevens.zaakUUID = zaak.uuid;
        gegevens.betrokkeneIdentificatie = betrokkeneIdentificatie;
        return this.http.post<void>(`${this.basepath}/initiator`, gegevens).pipe(
            catchError(this.handleError)
        );
    }

    deleteInitiator(zaak: Zaak): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/${zaak.uuid}/initiator`).pipe(
            catchError(this.handleError)
        );
    }

    updateZaakGeometrie(uuid: string, zaak: Zaak): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/${uuid}/zaakgeometrie`, zaak).pipe(
            catchError(this.handleError)
        );
    }

    ontkoppelInformatieObject(zaakUUID: string, documentUUID: string, reden: string): Observable<void> {
        const gegevens = new DocumentOntkoppelGegevens(zaakUUID, documentUUID, reden);
        return this.http.put<void>(`${this.basepath}/zaakinformatieobjecten/ontkoppel`, gegevens).pipe(
            catchError(this.handleError)
        );
    }

    findZakenForInformatieobject(documentUUID: string): Observable<string[]> {
        return this.http.get<string[]>(`${this.basepath}/zaken/informatieobject/${documentUUID}`).pipe(
            catchError(this.handleError)
        );
    }

    toekennenAanIngelogdeMedewerkerVanuitLijst(zaak: ZaakOverzicht | ZaakZoekObject, reden?: string): Observable<ZaakOverzicht> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.reden = reden;

        return this.http.put<ZaakOverzicht>(`${this.basepath}/toekennen/mij/lijst`, toekennenGegevens).pipe(
            catchError(this.handleError)
        );
    }

    listHistorieVoorZaak(uuid: string): Observable<HistorieRegel[]> {
        return this.http.get<HistorieRegel[]>(`${this.basepath}/zaak/${uuid}/historie`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    afbreken(uuid: string, zaakbeeindigReden: ZaakbeeindigReden): Observable<void> {
        const zaakAfbrekenGegevens = new ZaakAfbrekenGegevens();
        zaakAfbrekenGegevens.zaakUUID = uuid;
        zaakAfbrekenGegevens.zaakbeeindigRedenId = zaakbeeindigReden.id;

        return this.http.put<void>(`${this.basepath}/afbreken`, zaakAfbrekenGegevens).pipe(
            catchError(this.handleError)
        );
    }

    listCommunicatiekanalen(): Observable<string[]> {
        return this.http.get<string[]>(`${this.basepath}/communicatiekanalen`).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
