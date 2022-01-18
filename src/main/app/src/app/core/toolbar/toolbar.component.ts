/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {Observable, Subscription} from 'rxjs';
import {SignaleringenService} from '../../signaleringen.service';
import {SessionStorageService} from '../../shared/storage/session-storage.service';

@Component({
    selector: 'zac-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.less']
})
export class ToolbarComponent implements OnInit, OnDestroy {

    headerTitle$: Observable<string>;
    numberOfSignaleringen: number;
    ingelogdeMedewerker$: Observable<Medewerker>;

    subscription$: Subscription;

    constructor(public utilService: UtilService, public navigation: NavigationService, private identityService: IdentityService,
                private signaleringenService: SignaleringenService, private sessionStorage: SessionStorageService) {
    }

    ngOnInit(): void {
        this.headerTitle$ = this.utilService.headerTitle$;
        this.ingelogdeMedewerker$ = this.identityService.readIngelogdeMedewerker();
        this.subscription$ = this.signaleringenService.numberOfSignaleringenMedewerker$.subscribe(
            numberOfSignaleringen => {
                const numberOfSignaleringenStorage: number = this.sessionStorage.getSessionStorage('numberOfSignaleringen', 0);
                this.numberOfSignaleringen = numberOfSignaleringen - numberOfSignaleringenStorage;
            });
    }

    ngOnDestroy(): void {
        this.subscription$.unsubscribe();
    }

    updateNumberOfSignaleringen(): void {
        this.sessionStorage.setSessionStorage('numberOfSignaleringen', this.numberOfSignaleringen);
        this.numberOfSignaleringen = 0;
    }
}
