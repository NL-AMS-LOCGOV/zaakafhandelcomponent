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

    /** Cards will be added to the grid on the dashboard in this order */
    private cards: Array<DashboardCardData> = [
        new DashboardCardData('ZAAK', SignaleringType.ZAAK_OP_NAAM, 'dashboard.mijn.nieuwe.zaken'),
        new DashboardCardData('TAAK', SignaleringType.TAAK_OP_NAAM, 'dashboard.mijn.nieuwe.taken'),
        new DashboardCardData('ZAAK', SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD, 'dashboard.mijn.nieuwe.documenten')
    ];

    width: number; // Maximum number of cards horizontally

    /** Based on the screen size, switch from standard to one column per row */
    grid: Array<DashboardCardData[]> = [];

    constructor(private breakpointObserver: BreakpointObserver, private utilService: UtilService,
                private signaleringenService: SignaleringenService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.dashboard');
        this.width = SessionStorageUtil.getItem('dashboardWidth', 2);
        this.addCards();
        // TODO instead of session storage use userpreferences in a db
        SessionStorageUtil.setItem('dashboardOpened', moment());
        this.signaleringenService.updateSignaleringen();
    }

    private addCards(): void {
        this.signaleringenService.listDashboardSignaleringTypen().subscribe(typen => {
            for (const card of this.cards) {
                for (const type of typen) {
                    if (card.signaleringType === type) {
                        this.addCard(card);
                    }
                }
            }
        });
    }

    private addCard(card: DashboardCardData): void {
        if (this.grid.length < 1 || this.width <= this.grid[this.grid.length - 1].length) {
            this.grid.push([]);
        }
        this.grid[this.grid.length - 1].push(card);
    }
}
