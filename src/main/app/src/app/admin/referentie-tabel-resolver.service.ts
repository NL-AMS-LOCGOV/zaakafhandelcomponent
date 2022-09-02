/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {ReferentieTabelBeheerService} from './referentie-tabel-beheer.service';
import {ReferentieTabel} from './model/referentie-tabel';

@Injectable({
    providedIn: 'root'
})
export class ReferentieTabelResolver implements Resolve<ReferentieTabel> {

    constructor(private adminService: ReferentieTabelBeheerService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ReferentieTabel> {
        return this.adminService.readReferentieTabel(route.paramMap.get('id'));
    }
}
