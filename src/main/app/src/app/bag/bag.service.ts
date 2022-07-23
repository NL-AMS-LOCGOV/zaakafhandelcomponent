/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {ListNummeraanduidingenParameters} from './model/list-nummeraanduidingen-parameters';
import {Nummeraanduiding} from './model/nummeraanduiding';
import {Resultaat} from '../shared/model/resultaat';
import {catchError, Observable} from 'rxjs';
import {BAGObjectGegevens} from './model/bagobject-gegevens';

@Injectable({
    providedIn: 'root'
})
export class BAGService {

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    private basepath = '/rest/bag';

    listNummeraanduidingen(listNummeraanduidingenParameters: ListNummeraanduidingenParameters): Observable<Resultaat<Nummeraanduiding>> {
        return this.http.put<Resultaat<Nummeraanduiding>>(`${this.basepath}/nummeraanduidingen`, listNummeraanduidingenParameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createBAGObject(bagObjectGegevens: BAGObjectGegevens): Observable<void> {
        return this.http.post<void>(`${this.basepath}`, bagObjectGegevens).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
