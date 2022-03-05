/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {Zaak} from './model/zaak';
import {ZakenService} from './zaken.service';

@Injectable({
    providedIn: 'root'
})
export class ZaakResolver implements Resolve<Zaak> {

    constructor(private zakenService: ZakenService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Zaak> {
        const zaakID: string = route.paramMap.get('identificatie');
        return this.zakenService.readZaakByID(zaakID);
    }
}
