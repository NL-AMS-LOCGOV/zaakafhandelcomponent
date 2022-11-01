/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {MailtemplateService} from './mailtemplate.service';
import {Mailtemplate} from './model/mailtemplate';

@Injectable({
    providedIn: 'root'
})
export class MailtemplateResolver implements Resolve<Mailtemplate> {

    constructor(private service: MailtemplateService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Mailtemplate> {
        return this.service.readMailtemplate(route.paramMap.get('id'));
    }
}
