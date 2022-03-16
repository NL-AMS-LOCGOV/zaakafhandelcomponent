/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {BreakpointObserver} from '@angular/cdk/layout';
import {UtilService} from '../core/service/util.service';
import * as moment from 'moment';
import {SessionStorageService} from '../shared/storage/session-storage.service';
import {SignaleringenService} from '../signaleringen.service';
import {DashboardCardData} from './model/dashboard-card-data';
import {SignaleringType} from '../shared/signaleringen/signalering-type';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    zakenNieuwOpNaamCard: DashboardCardData = new DashboardCardData('dashboard.mijn.nieuwe.zaken',
        SignaleringType.ZAAK_OP_NAAM, 'ZAAK');
    takenNieuwOpNaamCard: DashboardCardData = new DashboardCardData('dashboard.mijn.nieuwe.taken',
        SignaleringType.TAAK_OP_NAAM, 'TAAK');

    /** Based on the screen size, switch from standard to one column per row */
    cards: Array<DashboardCardData[]> = [
        [this.zakenNieuwOpNaamCard, this.takenNieuwOpNaamCard]
    ];

    constructor(private breakpointObserver: BreakpointObserver, private utilService: UtilService, private sessionStorageService: SessionStorageService,
                private signaleringenService: SignaleringenService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.dashboard');
        // TODO instead of session storage use userpreferences in a db
        this.sessionStorageService.setSessionStorage('dashboardOpened', moment());
        this.signaleringenService.updateSignaleringen();
    }
}
