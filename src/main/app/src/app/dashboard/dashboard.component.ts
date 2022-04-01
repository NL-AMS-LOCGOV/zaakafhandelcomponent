/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {BreakpointObserver} from '@angular/cdk/layout';
import {UtilService} from '../core/service/util.service';
import * as moment from 'moment';
import {SignaleringenService} from '../signaleringen.service';
import {DashboardCardData} from './model/dashboard-card-data';
import {SignaleringType} from '../shared/signaleringen/signalering-type';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    zakenNieuwOpNaamCard: DashboardCardData = new DashboardCardData('dashboard.mijn.nieuwe.zaken',
        SignaleringType.ZAAK_OP_NAAM, 'ZAAK');
    takenNieuwOpNaamCard: DashboardCardData = new DashboardCardData('dashboard.mijn.nieuwe.taken',
        SignaleringType.TAAK_OP_NAAM, 'TAAK');
    zakenMetNieuweDocumentenCard: DashboardCardData = new DashboardCardData('dashboard.mijn.nieuwe.documenten',
        SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD, 'ZAAK');

    /** Based on the screen size, switch from standard to one column per row */
    cards: Array<DashboardCardData[]> = [
        [this.zakenNieuwOpNaamCard, this.takenNieuwOpNaamCard],
        [this.zakenMetNieuweDocumentenCard]
    ];

    constructor(private breakpointObserver: BreakpointObserver, private utilService: UtilService,
                private signaleringenService: SignaleringenService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.dashboard');
        // TODO instead of session storage use userpreferences in a db
        SessionStorageUtil.setItem('dashboardOpened', moment());
        this.signaleringenService.updateSignaleringen();
    }
}
