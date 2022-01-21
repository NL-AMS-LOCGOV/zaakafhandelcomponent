/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {Observable} from 'rxjs';
import {SignaleringenService} from '../../signaleringen.service';

@Component({
    selector: 'zac-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.less']
})
export class ToolbarComponent implements OnInit {

    headerTitle$: Observable<string>;
    hasNewSignaleringen$: Observable<boolean>;
    ingelogdeMedewerker$: Observable<Medewerker>;

    constructor(public utilService: UtilService, public navigation: NavigationService, private identityService: IdentityService,
                private signaleringenService: SignaleringenService) {
    }

    ngOnInit(): void {
        this.headerTitle$ = this.utilService.headerTitle$;
        this.ingelogdeMedewerker$ = this.identityService.readIngelogdeMedewerker();
        this.hasNewSignaleringen$ = this.signaleringenService.hasNewSignaleringen$;
    }
}
