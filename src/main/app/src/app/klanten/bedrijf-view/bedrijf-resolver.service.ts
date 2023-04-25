/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {Bedrijf} from '../model/bedrijven/bedrijf';
import {KlantenService} from '../klanten.service';

@Injectable({
    providedIn: 'root'
})
export class BedrijfResolverService implements Resolve<Bedrijf> {

    constructor(private klantenService: KlantenService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Bedrijf> {
        const id: string = route.paramMap.get('vesOrRSIN');
        return this.klantenService.readBedrijf(id);
    }
}
