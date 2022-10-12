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
import {DashboardCardType} from './model/dashboard-card-type';
import {DashboardCardId} from './model/dashboard-card-id';
import {CdkDragDrop, moveItemInArray, transferArrayItem} from '@angular/cdk/drag-drop';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    /** These cards may be added to the grid on the dashboard, initially in this order */
    private cards: Array<DashboardCard> = [
        new DashboardCard(DashboardCardId.MIJN_DOCUMENTEN_NIEUW, DashboardCardType.ZAKEN, SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD),
        new DashboardCard(DashboardCardId.MIJN_TAKEN, DashboardCardType.TAAK_ZOEKEN),
        new DashboardCard(DashboardCardId.MIJN_TAKEN_NIEUW, DashboardCardType.TAKEN, SignaleringType.TAAK_OP_NAAM),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN, DashboardCardType.ZAAK_ZOEKEN),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN_NIEUW, DashboardCardType.ZAKEN, SignaleringType.ZAAK_OP_NAAM),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN_WAARSCHUWING, DashboardCardType.ZAAK_WAARSCHUWINGEN, SignaleringType.ZAAK_VERLOPEND)
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
        this.loadCards();
        // TODO instead of session storage use userpreferences in a db
        SessionStorageUtil.setItem('dashboardOpened', moment());
        this.signaleringenService.updateSignaleringen();
    }

    drop(event: CdkDragDrop<DashboardCard[]>) {
        const sameRow: boolean = event.previousContainer.data === event.container.data;
        const sameColumn: boolean = event.previousIndex === event.currentIndex;
        if (!sameRow || !sameColumn) {
            if (sameRow) {
                moveItemInArray(
                    event.container.data,
                    event.previousIndex,
                    event.currentIndex
                );
            } else {
                transferArrayItem(
                    event.previousContainer.data,
                    event.container.data,
                    event.previousIndex,
                    event.currentIndex
                );
            }
            this.flow();
            this.trim();
            this.saveCards();
        }
    }

    private flow() {
        for (let row = 0; row < this.grid.length; row++) {
            const next: number = row + 1;
            const cards = this.grid[row];
            while (this.width < cards.length) {
                if (next === this.grid.length) {
                    this.grid.push([]);
                }
                this.grid[next].unshift(cards.pop());
            }
            for (let column = 0; column < cards.length; column++) {
                if (cards[column] == null) {
                    cards[column] = this.pull(row, column + 1);
                }
            }
            while (cards.length < this.width) {
                cards.push(this.pull(next, 0));
            }
        }
    }

    private pull(row: number, column: number): DashboardCard {
        if (row < this.grid.length) {
            if (column < this.grid[row].length) {
                if (this.grid[row][column] != null) {
                    const card = this.grid[row][column];
                    this.grid[row][column] = null;
                    return card;
                }
                return this.pull(row, column + 1);
            }
            return this.pull(row + 1, 0);
        }
        return null;
    }

    private trim() {
        for (const row of this.grid) {
            while (0 < row.length && row[row.length - 1] == null) {
                row.length--;
            }
        }
        while (0 < this.grid.length && this.grid[this.grid.length - 1].length < 1) {
            this.grid.length--;
        }
    }

    private saveCards(): void {
        // TODO 1697
    }

    private loadCards(): void {
        // TODO 1697
        this.signaleringenService.listDashboardSignaleringTypen().subscribe(typen => {
            for (const card of this.cards) {
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
        const rows = this.grid.length;
        if (rows < 1) {
            this.setCard(0, 0, card);
        } else {
            const columns = this.grid[rows - 1].length;
            if (this.width <= columns) {
                this.setCard(rows, 0, card);
            } else {
                this.setCard(rows - 1, columns, card);
            }
        }
    }

    private setCard(row: number, column: number, card: DashboardCard): void {
        while (this.grid.length <= row) {
            this.grid.push([]);
        }
        this.grid[row][column] = card;
    }
}
