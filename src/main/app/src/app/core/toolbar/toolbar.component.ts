/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {UtilService} from '../service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';

@Component({
    selector: 'zac-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.less']
})
export class ToolbarComponent implements OnInit {

    headerTitle: string;
    ingelogdeMedewerker: Medewerker;

    constructor(public utilService: UtilService, public navigation: NavigationService, private identityService: IdentityService) {
    }

    ngOnInit(): void {
        this.utilService.headerTitle$.subscribe(value => this.headerTitle = value);

        this.identityService.getIngelogdeMedewerker().subscribe(medewerker => {
            this.ingelogdeMedewerker = medewerker;
        });
    }
}
