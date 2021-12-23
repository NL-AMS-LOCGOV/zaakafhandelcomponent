/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {AdminService} from './admin.service';
import {Zaaktype} from '../zaken/model/zaaktype';
import {ZaakafhandelParameters} from './model/zaakafhandel-parameters';

@Injectable({
    providedIn: 'root'
})
export class ZaakafhandelParametersResolver implements Resolve<ZaakafhandelParameters> {

    constructor(private adminService: AdminService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ZaakafhandelParameters> {
        return this.adminService.readZaakafhandelparameters(route.paramMap.get('uuid'));
    }
}
