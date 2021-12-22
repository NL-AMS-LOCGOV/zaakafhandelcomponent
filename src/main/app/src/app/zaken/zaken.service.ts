/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {Zaak} from './model/zaak';
import {Observable} from 'rxjs';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {TableRequest} from '../shared/dynamic-table/datasource/table-request';
import {TableResponse} from '../shared/dynamic-table/datasource/table-response';
import {Zaaktype} from './model/zaaktype';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {ZaakOverzicht} from './model/zaak-overzicht';
import {ZaakToekennenGegevens} from './model/zaak-toekennen-gegevens';
import {Medewerker} from '../identity/model/medewerker';
import {ZakenVerdeelGegevens} from './model/zaken-verdeel-gegevens';
import {AuditTrailRegel} from '../shared/audit/model/audit-trail-regel';
import {Groep} from '../identity/model/groep';

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

    createZaak(zaak: Zaak): Observable<Zaak> {
        return this.http.post<Zaak>(`${this.basepath}/zaak`, zaak).pipe(
            catchError(this.handleError)
        );
    }

    partialUpdateZaak(uuid: string, zaak: Zaak): Observable<Zaak> {
        return this.http.patch<Zaak>(`${this.basepath}/zaak/${uuid}`, zaak).pipe(
            catchError(this.handleError)
        );
    }

    listZakenWerkvoorraad(request: TableRequest): Observable<TableResponse<ZaakOverzicht>> {
        return this.http.get<TableResponse<ZaakOverzicht>>(`${this.basepath}/werkvoorraad`, {
            params: ZakenService.getTableParams(request)
        }).pipe(
            catchError(this.handleError)
        );
    }

    listZakenMijn(request: TableRequest): Observable<TableResponse<ZaakOverzicht>> {
        return this.http.get<TableResponse<ZaakOverzicht>>(`${this.basepath}/mijn`, {
            params: ZakenService.getTableParams(request)
        }).pipe(
            catchError(this.handleError)
        );
    }

    listZakenAfgehandeld(request: TableRequest): Observable<TableResponse<ZaakOverzicht>> {
        return this.http.get<TableResponse<ZaakOverzicht>>(`${this.basepath}/afgehandeld`, {
            params: ZakenService.getTableParams(request)
        }).pipe(
            catchError(this.handleError)
        );
    }

    listZaaktypes(): Observable<Zaaktype[]> {
        return this.http.get<Zaaktype[]>(`${this.basepath}/zaaktypes`).pipe(
            catchError(this.handleError)
        );
    }

    toekennen(zaak: Zaak): Observable<Zaak> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.zaakUUID = zaak.uuid;
        zaakBody.behandelaarGebruikersnaam = zaak.behandelaar?.gebruikersnaam;

        return this.http.put<Zaak>(`${this.basepath}/toekennen`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    toekennenGroep(zaak: Zaak): Observable<Zaak> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.zaakUUID = zaak.uuid;
        zaakBody.groepId = zaak.groep?.id;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/groep`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    verdelen(zaken: ZaakOverzicht[], medewerker: Medewerker, groep: Groep): Observable<void> {
        const zaakBody: ZakenVerdeelGegevens = new ZakenVerdeelGegevens();
        zaakBody.uuids = zaken.map(zaak => zaak.uuid);
        zaakBody.behandelaarGebruikersnaam = medewerker?.gebruikersnaam;
        zaakBody.groepId = groep?.id;

        return this.http.put<void>(`${this.basepath}/verdelen`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    vrijgeven(zaken: ZaakOverzicht[]): Observable<void> {
        return this.http.put<void>(`${this.basepath}/vrijgeven`, zaken.map(zaak => zaak.uuid)).pipe(
            catchError(this.handleError)
        );
    }

    toekennenAanIngelogdeMedewerker(zaak: Zaak): Observable<Zaak> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.zaakUUID = zaak.uuid;

        return this.http.put<Zaak>(`${this.basepath}/toekennen/mij`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    toekennenAanIngelogdeMedewerkerVanuitLijst(zaak: ZaakOverzicht): Observable<ZaakOverzicht> {
        const zaakBody: ZaakToekennenGegevens = new ZaakToekennenGegevens();
        zaakBody.zaakUUID = zaak.uuid;

        return this.http.put<ZaakOverzicht>(`${this.basepath}/toekennen/mij/lijst`, zaakBody).pipe(
            catchError(this.handleError)
        );
    }

    listAuditTrailVoorZaak(uuid: string): Observable<AuditTrailRegel[]> {
        return this.http.get<AuditTrailRegel[]>(`${this.basepath}/zaak/${uuid}/auditTrail`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
