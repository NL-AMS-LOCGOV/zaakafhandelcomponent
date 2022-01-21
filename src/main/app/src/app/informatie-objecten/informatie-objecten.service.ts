/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {EnkelvoudigInformatieobject} from './model/enkelvoudig-informatieobject';
import {ZaakInformatieobject} from './model/zaak-informatieobject';
import {Informatieobjecttype} from './model/informatieobjecttype';
import {AuditTrailRegel} from '../shared/audit/model/audit-trail-regel';
import {EnkelvoudigInformatieObjectZoekParameters} from './model/enkelvoudig-informatie-object-zoek-parameters';

@Injectable({
    providedIn: 'root'
})
export class InformatieObjectenService {

    private basepath = '/rest/informatieobjecten';
    public uploadUrl = this.basepath + '/informatieobject/upload/{zaakUuid}';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    readEnkelvoudigInformatieobject(uuid: string): Observable<EnkelvoudigInformatieobject> {
        return this.http.get<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listInformatieobjecttypes(zaakTypeID): Observable<Informatieobjecttype[]> {
        return this.http.get<Informatieobjecttype[]>(`${this.basepath}/informatieobjecttypes/${zaakTypeID}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    createEnkelvoudigInformatieobject(zaakUuid: string, infoObject: EnkelvoudigInformatieobject): Observable<EnkelvoudigInformatieobject> {
        return this.http.post<EnkelvoudigInformatieobject>(`${this.basepath}/informatieobject/${zaakUuid}`, infoObject).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listEnkelvoudigInformatieobjecten(zoekParameters: EnkelvoudigInformatieObjectZoekParameters): Observable<EnkelvoudigInformatieobject[]> {
        return this.http.post<EnkelvoudigInformatieobject[]>(`${this.basepath}/informatieobjectenList`, zoekParameters).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listZaakInformatieobjecten(uuid: string): Observable<ZaakInformatieobject[]> {
        return this.http.get<ZaakInformatieobject[]>(`${this.basepath}/informatieobject/${uuid}/zaken`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listAuditTrail(uuid: string): Observable<AuditTrailRegel[]> {
        return this.http.get<AuditTrailRegel[]>(`${this.basepath}/informatieobject/${uuid}/auditTrail`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
