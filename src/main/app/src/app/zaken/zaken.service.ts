/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Zaak} from './model/zaak';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
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
import {ZaakHeropenenGegevens} from './model/zaak-heropenen-gegevens';
import {ZaakAfsluitenGegevens} from './model/zaak-afsluiten-gegevens';
import {ZaakKoppelGegevens} from './model/zaak-koppel-gegevens';
import {ZaakOntkoppelGegevens} from './model/zaak-ontkoppel-gegevens';

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
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readZaakByID(id: string): Observable<Zaak> {
        return this.http.get<Zaak>(`${this.basepath}/zaak/id/${id}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createZaak(zaak: Zaak): Observable<Zaak> {
        return this.http.post<Zaak>(`${this.basepath}/zaak`, zaak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    updateZaak(uuid: string, zaak: Zaak, reden?: string): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${uuid}`, new ZaakEditMetRedenGegevens(zaak, reden)).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    readOpschortingZaak(uuid: string): Observable<ZaakOpschorting> {
        return this.http.get<ZaakOpschorting>(`${this.basepath}/zaak/${uuid}/opschorting`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    opschortenZaak(uuid: string, zaakOpschortGegevens: ZaakOpschortGegevens): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${uuid}/opschorting`, zaakOpschortGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    verlengenZaak(zaakUUID: string, zaakVerlengGegevens: ZaakVerlengGegevens): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${zaakUUID}/verlenging`, zaakVerlengGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }


    listZaakWaarschuwingen(): Observable<ZaakOverzicht[]> {
        return this.http.get<ZaakOverzicht[]>(`${this.basepath}/waarschuwing`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaaktypes(): Observable<Zaaktype[]> {
        return this.http.get<Zaaktype[]>(`${this.basepath}/zaaktypes`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    toekennen(zaak: Zaak, reden?: string): Observable<Zaak> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.behandelaarGebruikersnaam = zaak.behandelaar?.id;
        toekennenGegevens.reden = reden;

        return this.http.put<Zaak>(`${this.basepath}/toekennen`, toekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    toekennenGroep(zaak: Zaak, reden?: string): Observable<Zaak> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.groepId = zaak.groep?.id;
        toekennenGegevens.reden = reden;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/groep`, toekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    verdelen(uuids: string[], groep?: Group, medewerker?: User, reden?: string): Observable<void> {
        const verdeelGegevens: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
        verdeelGegevens.uuids = uuids;
        verdeelGegevens.groepId = groep?.id;
        verdeelGegevens.behandelaarGebruikersnaam = medewerker?.id;
        verdeelGegevens.reden = reden;

        return this.http.put<void>(`${this.basepath}/verdelen`, verdeelGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    vrijgeven(uuids: string[], reden?: string): Observable<void> {
        const verdeelGegevens: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
        verdeelGegevens.uuids = uuids;
        verdeelGegevens.reden = reden;

        return this.http.put<void>(`${this.basepath}/vrijgeven`, verdeelGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    toekennenAanIngelogdeMedewerker(zaak: Zaak, reden?: string): Observable<Zaak> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        toekennenGegevens.zaakUUID = zaak.uuid;
        toekennenGegevens.reden = reden;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/mij`, toekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createInitiator(zaak: Zaak, betrokkeneIdentificatie: string): Observable<void> {
        const gegevens = new ZaakBetrokkeneGegevens();
        gegevens.zaakUUID = zaak.uuid;
        gegevens.betrokkeneIdentificatie = betrokkeneIdentificatie;
        return this.http.post<void>(`${this.basepath}/initiator`, gegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    deleteInitiator(zaak: Zaak): Observable<void> {
        return this.http.delete<void>(`${this.basepath}/${zaak.uuid}/initiator`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    updateZaakGeometrie(uuid: string, zaak: Zaak): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/${uuid}/zaakgeometrie`, zaak).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    ontkoppelInformatieObject(zaakUUID: string, documentUUID: string, reden: string): Observable<void> {
        const gegevens = new DocumentOntkoppelGegevens(zaakUUID, documentUUID, reden);
        return this.http.put<void>(`${this.basepath}/zaakinformatieobjecten/ontkoppel`, gegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    toekennenAanIngelogdeMedewerkerVanuitLijst(zaak: ZaakOverzicht | ZaakZoekObject, reden?: string): Observable<ZaakOverzicht> {
        const toekennenGegevens: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        if ('id' in zaak) {
            toekennenGegevens.zaakUUID = zaak.id;
        } else {
            toekennenGegevens.zaakUUID = zaak.uuid;
        }
        toekennenGegevens.reden = reden;

        return this.http.put<ZaakOverzicht>(`${this.basepath}/toekennen/mij/lijst`, toekennenGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listHistorieVoorZaak(uuid: string): Observable<HistorieRegel[]> {
        return this.http.get<HistorieRegel[]>(`${this.basepath}/zaak/${uuid}/historie`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    afbreken(uuid: string, beeindigReden: ZaakbeeindigReden): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/zaak/${uuid}/afbreken`, new ZaakAfbrekenGegevens(beeindigReden.id)).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    heropenen(uuid: string, heropenReden: string): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/zaak/${uuid}/heropenen`, new ZaakHeropenenGegevens(heropenReden)).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    afsluiten(uuid: string, afsluitenReden: string, resultaattypeUuid: string): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/zaak/${uuid}/afsluiten`, new ZaakAfsluitenGegevens(afsluitenReden, resultaattypeUuid)).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listCommunicatiekanalen(): Observable<string[]> {
        return this.http.get<string[]>(`${this.basepath}/communicatiekanalen`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    koppelZaak(zaakKoppelGegevens: ZaakKoppelGegevens): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/zaak/koppel`, zaakKoppelGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    ontkoppelZaak(zaakOntkoppelGegevens: ZaakOntkoppelGegevens): Observable<void> {
        return this.http.patch<void>(`${this.basepath}/zaak/ontkoppel`, zaakOntkoppelGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
