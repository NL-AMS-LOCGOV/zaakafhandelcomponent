/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {FoutAfhandelingService} from '../../fout-afhandeling/fout-afhandeling.service';
import {catchError} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LocationService {

    private readonly flSuggest: string = 'type,weergavenaam,id';

    private readonly typeSuggest: string = 'type:adres';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    addressSuggest(zoekOpdracht) {
        const url = `https://geodata.nationaalgeoregister.nl/locatieserver/v3/suggest?wt=json&q=${zoekOpdracht}&fl=${this.flSuggest}&fq=${this.typeSuggest}&rows=5`;
        return this.http.get<any>(url, {headers: new HttpHeaders({'Content-Type': 'application/json'})})
                   .pipe(catchError(this.handleError));
    }

    geolocationToAddress(coordinates) {
        const url = `https://geodata.nationaalgeoregister.nl/locatieserver/v3/suggest?wt=json&rows=1&fq=${this.typeSuggest}&fl=${this.flSuggest}&lon=${coordinates[0]}&lat=${coordinates[1]}`;
        return this.http.get<any>(url, {headers: new HttpHeaders({'Content-Type': 'application/json'})})
                   .pipe(catchError(this.handleError));
    }

    addressLookup(objectId) {
        const url = `https://geodata.nationaalgeoregister.nl/locatieserver/v3/lookup?wt=json&id=${objectId}`;
        return this.http.get<any>(url, {headers: new HttpHeaders({'Content-Type': 'application/json'})})
                   .pipe(catchError(this.handleError));
    }

    private handleError(err: HttpErrorResponse): Observable<never> {
        return this.foutAfhandelingService.redirect(err);
    }
}
