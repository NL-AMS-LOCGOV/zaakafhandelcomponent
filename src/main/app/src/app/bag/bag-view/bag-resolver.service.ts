/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {BAGObject} from '../model/bagobject';
import {BAGService} from '../bag.service';

@Injectable({
    providedIn: 'root'
})
export class BAGResolverService implements Resolve<BAGObject> {

    constructor(private bagService: BAGService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<BAGObject> {
        const type: string = route.paramMap.get('type');
        const id: string = route.paramMap.get('id');
        return this.bagService.read(type.toUpperCase(), id);
    }
}
