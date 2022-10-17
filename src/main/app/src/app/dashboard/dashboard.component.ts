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
import {GebruikersvoorkeurenService} from '../gebruikersvoorkeuren/gebruikersvoorkeuren.service';
import {forkJoin} from 'rxjs';
import {DashboardCardInstelling} from './model/dashboard-card-instelling';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    /** These are the cards that may be put in the grid on the dashboard */
    private cards: Array<DashboardCard> = [
        new DashboardCard(DashboardCardId.MIJN_DOCUMENTEN_NIEUW, DashboardCardType.ZAKEN, SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD),
        new DashboardCard(DashboardCardId.MIJN_TAKEN, DashboardCardType.TAAK_ZOEKEN),
        new DashboardCard(DashboardCardId.MIJN_TAKEN_NIEUW, DashboardCardType.TAKEN, SignaleringType.TAAK_OP_NAAM),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN, DashboardCardType.ZAAK_ZOEKEN),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN_NIEUW, DashboardCardType.ZAKEN, SignaleringType.ZAAK_OP_NAAM),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN_WAARSCHUWING, DashboardCardType.ZAAK_WAARSCHUWINGEN, SignaleringType.ZAAK_VERLOPEND)
    ];

    dashboardCardType = DashboardCardType;
    width: number; // Maximum number of cards horizontally
    showHint: boolean = false; // Show hint how to add signalerings cards

    /** Based on the screen size, switch from standard to one column per row */
    instellingen: DashboardCardInstelling[];
    grid: Array<DashboardCard[]> = [];

    constructor(private breakpointObserver: BreakpointObserver,
                private utilService: UtilService,
                private signaleringenService: SignaleringenService,
                private gebruikersvoorkeurenService: GebruikersvoorkeurenService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.dashboard');
        this.width = SessionStorageUtil.getItem('dashboardWidth', 2);
        this.loadCards();
        // TODO instead of session storage use userpreferences in a db
        SessionStorageUtil.setItem('dashboardOpened', moment());
        this.signaleringenService.updateSignaleringen();
    }

    private loadCards(): void {
        forkJoin([
            this.gebruikersvoorkeurenService.listDashboardCards(),
            this.signaleringenService.listDashboardSignaleringTypen()
        ]).subscribe(([settings, signaleringenSettings]) => {
            this.instellingen = settings;
            this.addExistingCards(settings, signaleringenSettings);
            this.addNewCards(signaleringenSettings);
            this.showHint = this.grid.length === 0;
        });
    }

    // add configured cards (except when disabled by corresponding signaleringen settings)
    private addExistingCards(settings, signaleringenSettings): void {
        for (const instelling of settings) {
            for (const card of this.cards) {
                if (card.id === instelling.cardId) {
                    if (card.signaleringType == null) {
                        this.addCard(card);
                    } else {
                        const i: number = signaleringenSettings.indexOf(card.signaleringType);
                        if (0 <= i) {
                            this.addCard(card);
                            signaleringenSettings.splice(i, 1); // prevent adding this one as new card
                        }
                    }
                    break;
                }
            }
        }
    }

    // add unconfigured cards (when enabled by the corresponding signaleringen settings)
    private addNewCards(signaleringenSettings): void {
        for (const signaleringType of signaleringenSettings) {
            for (const card of this.cards) {
                if (card.signaleringType === signaleringType) {
                    this.addCard(card);
                    break;
                }
            }
        }
    }

    private addCard(card: DashboardCard): void {
        const rows = this.grid.length;
        if (rows < 1) {
            this.putCard(card);
        } else {
            const columns = this.grid[rows - 1].length;
            if (this.width <= columns) {
                this.putCard(card, rows);
            } else {
                this.putCard(card, rows - 1, columns);
            }
        }
    }

    private putCard(card: DashboardCard, row: number = 0, column: number = 0): void {
        while (this.grid.length <= row) {
            this.grid.push([]);
        }
        this.grid[row][column] = card;
    }

    drop(event: CdkDragDrop<DashboardCard[]>) {
        const sameRow = event.previousContainer.data === event.container.data;
        const sameColumn = event.previousIndex === event.currentIndex;
        if (!sameRow || !sameColumn) {
            if (sameRow) {
                moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
            } else {
                transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
            }
            this.flow();
            this.saveCards();
        }
    }

    private flow() {
        for (let row = 0; row < this.grid.length; row++) {
            this.flush(row);
            this.compact(row);
            this.fill(row);
        }
        this.trim();
    }

    // make sure the row does not contain too many cards (by pushing cards to the next row)
    private flush(row: number): void {
        const cards = this.grid[row];
        while (this.width < cards.length) {
            if (row === this.grid.length - 1) {
                this.grid.push([]);
            }
            this.grid[row + 1].unshift(cards.pop());
        }
    }

    // remove empty cells from the row (by pulling cards from the rest of the grid)
    private compact(row: number): void {
        const cards = this.grid[row];
        for (let column = 0; column < cards.length; column++) {
            if (cards[column] == null) {
                cards[column] = this.pull(row, column + 1);
            }
        }
    }

    // try to fill the row with cards (by pulling cards from the rest of the grid)
    private fill(row: number): void {
        const cards = this.grid[row];
        while (cards.length < this.width) {
            cards.push(this.pull(row + 1, 0));
        }
    }

    // find the first card, i.e. a nonempty cell in the grid starting from the given position
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

    // remove trailing empty cells and rows from the grid
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
}
