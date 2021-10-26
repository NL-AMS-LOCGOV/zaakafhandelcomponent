/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {Subscription} from 'rxjs';

@Component({
    selector: 'zac-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.less']
})
export class ToolbarComponent implements OnInit, OnDestroy {

    headerTitle: string;
    ingelogdeMedewerker: Medewerker;

    subscription$: Subscription;

    constructor(public utilService: UtilService, public navigation: NavigationService, private identityService: IdentityService) {
    }

    ngOnInit(): void {
        this.subscription$ = this.utilService.headerTitle$.subscribe(value => this.headerTitle = value);

        this.identityService.getIngelogdeMedewerker().subscribe(medewerker => {
            this.ingelogdeMedewerker = medewerker;
        });
    }

    ngOnDestroy(): void {
        this.subscription$.unsubscribe();
    }
}
