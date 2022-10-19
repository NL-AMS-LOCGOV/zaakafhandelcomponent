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

    /** all cards that may be put on the dashboard */
    private cards: Array<DashboardCard> = [
        new DashboardCard(DashboardCardId.MIJN_TAKEN, DashboardCardType.TAAK_ZOEKEN),
        new DashboardCard(DashboardCardId.MIJN_TAKEN_NIEUW, DashboardCardType.TAKEN, SignaleringType.TAAK_OP_NAAM),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN, DashboardCardType.ZAAK_ZOEKEN),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN_NIEUW, DashboardCardType.ZAKEN, SignaleringType.ZAAK_OP_NAAM),
        new DashboardCard(DashboardCardId.MIJN_DOCUMENTEN_NIEUW, DashboardCardType.ZAKEN, SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD),
        new DashboardCard(DashboardCardId.MIJN_ZAKEN_WAARSCHUWING, DashboardCardType.ZAAK_WAARSCHUWINGEN, SignaleringType.ZAAK_VERLOPEND)
    ];

    dashboardCardType = DashboardCardType;
    width: number; // maximum number of cards horizontally
    editmode: boolean = false;

    instellingen: DashboardCardInstelling[]; // the last loaded card settings
    available: DashboardCard[] = []; // cards that are not on the dashboard
    grid: Array<DashboardCard[]> = []; // cards that are on the dashboard

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
        ]).subscribe(([dashboardInstellingen, signaleringInstellingen]) => {
            this.instellingen = dashboardInstellingen;
            this.addExistingCards(dashboardInstellingen, signaleringInstellingen);
            this.addNewCards(signaleringInstellingen);
            this.updateAvailable();
        });
    }

    // add configured cards (except when disabled by corresponding signaleringen settings)
    private addExistingCards(dashboardInstellingen, signaleringenInstellingen): void {
        for (const instelling of dashboardInstellingen) {
            for (const card of this.cards) {
                if (card.id === instelling.cardId) {
                    if (card.signaleringType == null) {
                        this.addCard(card);
                    } else {
                        const i: number = signaleringenInstellingen.indexOf(card.signaleringType);
                        if (0 <= i) {
                            this.addCard(card);
                            signaleringenInstellingen.splice(i, 1); // prevent adding this one as a new card in the next step
                        }
                    }
                    break;
                }
            }
        }
    }

    // add unconfigured cards (when enabled by the corresponding signaleringen settings)
    private addNewCards(signaleringenInstellingen): void {
        for (const signaleringType of signaleringenInstellingen) {
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

    private updateAvailable(): void {
        this.available = [];
        for (const card of this.cards) {
            if (this.isAvailable(card)) {
                this.available.push(card);
            }
        }
    }

    private isAvailable(card: DashboardCard): boolean {
        for (const row of this.grid) {
            for (const cell of row) {
                if (cell.id === card.id) {
                    return false;
                }
            }
        }
        return true;
    }

    move(event: CdkDragDrop<DashboardCard[]>) {
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

    add(card: DashboardCard): void {
        this.addCard(card);
        this.saveCard(card);
        this.updateAvailable();
    }

    delete(card: DashboardCard): void {
        for (const row of this.grid) {
            for (let column = 0; column < row.length; column++) {
                if (row[column].id === card.id) {
                    row[column] = null;
                    this.flow();
                    this.deleteCard(card);
                    this.updateAvailable();
                    return;
                }
            }
        }
    }

    private saveCards(): void {
        const instellingen: DashboardCardInstelling[] = [];
        for (const row of this.grid) {
            for (const card of row) {
                instellingen.push(this.getInstelling(card));
            }
        }
        this.gebruikersvoorkeurenService.updateDashboardCards(instellingen).subscribe(dashboardInstellingen => {
            this.instellingen = dashboardInstellingen;
        });
    }

    private saveCard(card: DashboardCard): void {
        this.gebruikersvoorkeurenService.addDashboardCard(this.getInstelling(card)).subscribe(dashboardInstellingen => {
            this.instellingen = dashboardInstellingen;
        });
    }

    private deleteCard(card: DashboardCard): void {
        this.gebruikersvoorkeurenService.deleteDashboardCard(this.getInstelling(card)).subscribe(dashboardInstellingen => {
            this.instellingen = dashboardInstellingen;
        });
    }

    private getInstelling(card: DashboardCard): DashboardCardInstelling {
        for (const existingInstelling of this.instellingen) {
            if (existingInstelling.cardId === card.id) {
                existingInstelling.signaleringType = card.signaleringType;
                return existingInstelling;
            }
        }
        const newInstelling: DashboardCardInstelling = new DashboardCardInstelling();
        newInstelling.cardId = card.id;
        newInstelling.signaleringType = card.signaleringType;
        return newInstelling;
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
}
