/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FoutAfhandelingService} from '../fout-afhandeling/fout-afhandeling.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {EnkelvoudigInformatieObject} from './model/enkelvoudig-informatie-object';
import {ZaakInformatieObjectKoppeling} from './model/zaak-informatie-object-koppeling';
import {Informatieobjecttype} from './model/informatieobjecttype';
import {AuditTrailRegel} from '../shared/audit/model/audit-trail-regel';

@Injectable({
    providedIn: 'root'
})
export class InformatieObjectenService {

    private basepath: string = '/rest/informatieobjecten';
    public uploadUrl: string = this.basepath + '/informatieobject/upload/{zaakUuid}';

    constructor(private http: HttpClient, private foutAfhandelingService: FoutAfhandelingService) {
    }

    getEnkelvoudigInformatieObject(uuid: string): Observable<EnkelvoudigInformatieObject> {
        return this.http.get<EnkelvoudigInformatieObject>(`${this.basepath}/informatieobject/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getInformatieobjecttypes(zaakTypeID): Observable<Informatieobjecttype[]> {
        return this.http.get<Informatieobjecttype[]>(`${this.basepath}/informatieobjecttypes/${zaakTypeID}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    postEnkelvoudigInformatieObject(zaakUuid: string, infoObject: EnkelvoudigInformatieObject): Observable<EnkelvoudigInformatieObject> {
        return this.http.post<EnkelvoudigInformatieObject>(`${this.basepath}/informatieobject/${zaakUuid}`, infoObject).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getEnkelvoudigInformatieObjectenVoorZaak(uuid: string): Observable<EnkelvoudigInformatieObject[]> {
        return this.http.get<EnkelvoudigInformatieObject[]>(`${this.basepath}/zaak/${uuid}`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    getZaakKoppelingen(uuid: string): Observable<ZaakInformatieObjectKoppeling[]> {
        return this.http.get<ZaakInformatieObjectKoppeling[]>(`${this.basepath}/informatieobject/${uuid}/zaken`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }

    listAuditTrail(uuid: string): Observable<AuditTrailRegel[]> {
        return this.http.get<AuditTrailRegel[]>(`${this.basepath}/informatieobject/${uuid}/auditTrail`).pipe(
            catchError(err => this.foutAfhandelingService.redirect(err))
        );
    }
}
