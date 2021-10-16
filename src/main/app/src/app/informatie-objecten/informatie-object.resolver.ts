/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieObject} from './model/enkelvoudig-informatie-object';
import {InformatieObjectenService} from './informatie-objecten.service';

@Injectable({
    providedIn: 'root'
})
export class InformatieObjectResolver implements Resolve<EnkelvoudigInformatieObject> {
    constructor(private informatieObjectenService: InformatieObjectenService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EnkelvoudigInformatieObject> {
        const informatieObjectUUID: string = route.paramMap.get('uuid');
        return this.informatieObjectenService.getEnkelvoudigInformatieObject(informatieObjectUUID);
    }
}
