/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {BreakpointObserver} from '@angular/cdk/layout';
import {UtilService} from '../core/service/util.service';
import * as moment from 'moment';
import {SignaleringenService} from '../signaleringen.service';
import {DashboardCard} from './model/dashboard-card';
import {SignaleringType} from '../shared/signaleringen/signalering-type';
import {SessionStorageUtil} from '../shared/storage/session-storage.util';
import {ObjectType} from './model/object-type';
import {DashboardCardType} from './model/dashboard-card-type';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    /** These cards may be added to the grid on the dashboard, initially in this order */
    private signaleringCards: Array<DashboardCard> = [
        new DashboardCard(DashboardCardType.MIJNDOCUMENTENNIEUW, ObjectType.ZAAK, SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD),
        new DashboardCard(DashboardCardType.MIJNTAKEN, ObjectType.TAAKZOEKRESULTAAT),
        new DashboardCard(DashboardCardType.MIJNTAKENNIEUW, ObjectType.TAAK, SignaleringType.TAAK_OP_NAAM),
        new DashboardCard(DashboardCardType.MIJNZAKEN, ObjectType.ZAAKZOEKRESULTAAT),
        new DashboardCard(DashboardCardType.MIJNZAKENNIEUW, ObjectType.ZAAK, SignaleringType.ZAAK_OP_NAAM),
        new DashboardCard(DashboardCardType.MIJNZAKENWAARSCHUWING, ObjectType.ZAAKWAARSCHUWING, SignaleringType.ZAAK_VERLOPEND)
    ];

    width: number; // Maximum number of cards horizontally
    showHint: boolean = false; // Show hint how to add signalerings cards

    /** Based on the screen size, switch from standard to one column per row */
    grid: Array<DashboardCard[]> = [];

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
        // TODO 1697
        this.signaleringenService.listDashboardSignaleringTypen().subscribe(typen => {
            for (const card of this.signaleringCards) {
                if (card.signaleringType == null) {
                    this.addCard(card);
                } else {
                    for (const type of typen) {
                        if (card.signaleringType === type) {
                            this.addCard(card);
                        }
                    }
                }
            }
            this.showHint = this.grid.length === 0;
        });
    }

    private addCard(card: DashboardCard): void {
        if (this.grid.length < 1 || this.width <= this.grid[this.grid.length - 1].length) {
            this.grid.push([]);
        }
        this.grid[this.grid.length - 1].push(card);
    }
}
