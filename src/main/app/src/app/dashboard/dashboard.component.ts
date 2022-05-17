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

    /** These cards may be added to the grid on the dashboard in this order */
    private signaleringCards: Array<DashboardCardData> = [
        new DashboardCardData('ZAAK', 'dashboard.mijn.zaken.nieuw', SignaleringType.ZAAK_OP_NAAM),
        new DashboardCardData('TAAK', 'dashboard.mijn.taken.nieuw', SignaleringType.TAAK_OP_NAAM),
        new DashboardCardData('ZAAK', 'dashboard.mijn.documenten.nieuw', SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD)
    ];

    /** These cards will then be added to the grid on the dashboard in this order */
    private otherCards: Array<DashboardCardData> = [
        new DashboardCardData('ZAAK-WAARSCHUWING', 'dashboard.mijn.zaken.waarschuwing')
    ];

    width: number; // Maximum number of cards horizontally
    showHint: boolean = false; // Show hint how to add signalerings cards

    /** Based on the screen size, switch from standard to one column per row */
    grid: Array<DashboardCardData[]> = [];

    constructor(private breakpointObserver: BreakpointObserver,
                private utilService: UtilService,
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
            for (const card of this.signaleringCards) {
                for (const type of typen) {
                    if (card.signaleringType === type) {
                        this.addCard(card);
                    }
                }
            }
            this.showHint = this.grid.length === 0;
            for (const card of this.otherCards) {
                this.addCard(card);
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
