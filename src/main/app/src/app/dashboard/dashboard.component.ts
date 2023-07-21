/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit, ViewChild } from "@angular/core";
import { BreakpointObserver } from "@angular/cdk/layout";
import { UtilService } from "../core/service/util.service";
import * as moment from "moment";
import { SignaleringenService } from "../signaleringen.service";
import { DashboardCard } from "./model/dashboard-card";
import { SignaleringType } from "../shared/signaleringen/signalering-type";
import { SessionStorageUtil } from "../shared/storage/session-storage.util";
import { DashboardCardType } from "./model/dashboard-card-type";
import { DashboardCardId } from "./model/dashboard-card-id";
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from "@angular/cdk/drag-drop";
import { GebruikersvoorkeurenService } from "../gebruikersvoorkeuren/gebruikersvoorkeuren.service";
import { forkJoin } from "rxjs";
import { DashboardCardInstelling } from "./model/dashboard-card-instelling";
import { MatMenuTrigger } from "@angular/material/menu";

@Component({
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.less"],
})
export class DashboardComponent implements OnInit {
  @ViewChild(MatMenuTrigger) menuTrigger: MatMenuTrigger;

  /** all cards that may be put on the dashboard */
  private cards: Array<DashboardCard> = [
    new DashboardCard(
      DashboardCardId.MIJN_TAKEN,
      DashboardCardType.TAAK_ZOEKEN,
    ),
    new DashboardCard(
      DashboardCardId.MIJN_TAKEN_NIEUW,
      DashboardCardType.TAKEN,
      SignaleringType.TAAK_OP_NAAM,
    ),
    new DashboardCard(
      DashboardCardId.MIJN_ZAKEN,
      DashboardCardType.ZAAK_ZOEKEN,
    ),
    new DashboardCard(
      DashboardCardId.MIJN_ZAKEN_NIEUW,
      DashboardCardType.ZAKEN,
      SignaleringType.ZAAK_OP_NAAM,
    ),
    new DashboardCard(
      DashboardCardId.MIJN_DOCUMENTEN_NIEUW,
      DashboardCardType.ZAKEN,
      SignaleringType.ZAAK_DOCUMENT_TOEGEVOEGD,
    ),
    new DashboardCard(
      DashboardCardId.MIJN_ZAKEN_WAARSCHUWING,
      DashboardCardType.ZAAK_WAARSCHUWINGEN,
      SignaleringType.ZAAK_VERLOPEND,
    ),
  ];

  dashboardCardType = DashboardCardType;
  width: number; // actual number of cards horizontally
  editmode: boolean;
  showHint: boolean;

  instellingen: DashboardCardInstelling[]; // the last loaded card settings
  available: DashboardCard[] = []; // cards that are not on the dashboard
  grid: Array<DashboardCard[]> = []; // cards that are on the dashboard

  constructor(
    private breakpointObserver: BreakpointObserver,
    private utilService: UtilService,
    private signaleringenService: SignaleringenService,
    private gebruikersvoorkeurenService: GebruikersvoorkeurenService,
  ) {}

  ngOnInit(): void {
    this.utilService.setTitle("title.dashboard");
    this.loadCards(SessionStorageUtil.getItem("dashboardWidth", 3));
    // TODO instead of session storage use userpreferences in a db
    SessionStorageUtil.setItem("dashboardOpened", moment());
    this.signaleringenService.updateSignaleringen();
  }

  private loadCards(width: number): void {
    while (this.grid.length < width) {
      this.grid.push([]);
    }
    forkJoin([
      this.gebruikersvoorkeurenService.listDashboardCards(),
      this.signaleringenService.listDashboardSignaleringTypen(),
    ]).subscribe(([dashboardInstellingen, signaleringInstellingen]) => {
      this.instellingen = dashboardInstellingen;
      this.addExistingCards(dashboardInstellingen, signaleringInstellingen);
      this.addNewCards(signaleringInstellingen);
      this.updateWidth();
      this.updateAvailable();
    });
  }

  // add configured cards (except when disabled by corresponding signaleringen settings)
  private addExistingCards(
    dashboardInstellingen,
    signaleringenInstellingen,
  ): void {
    for (const instelling of dashboardInstellingen) {
      for (const card of this.cards) {
        if (card.id === instelling.cardId) {
          if (card.signaleringType == null) {
            this.putCard(card, instelling.column);
          } else {
            const i: number = signaleringenInstellingen.indexOf(
              card.signaleringType,
            );
            if (0 <= i) {
              this.putCard(card, instelling.column);
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

  // find a good position for a new card
  private addCard(card: DashboardCard): Position {
    const columns = this.grid.length;
    let shortest = -1;
    for (let column = 0; column < columns; column++) {
      if (
        shortest < 0 ||
        this.grid[column].length < this.grid[shortest].length
      ) {
        shortest = column;
      }
    }
    return this.putCard(card, shortest);
  }

  private putCard(card: DashboardCard, column = 0): Position {
    return new Position(column, this.grid[column].push(card) - 1);
  }

  private updateWidth() {
    let width = 0;
    for (const column of this.grid) {
      if (0 < column.length) {
        width++;
      }
    }
    this.width = width;
  }

  private updateAvailable(): void {
    this.available = [];
    for (const card of this.cards) {
      if (this.isAvailable(card)) {
        this.available.push(card);
      }
    }
    this.showHint = this.available.length === this.cards.length;
  }

  private isAvailable(card: DashboardCard): boolean {
    for (const column of this.grid) {
      for (const row of column) {
        if (row.id === card.id) {
          return false;
        }
      }
    }
    return true;
  }

  move(event: CdkDragDrop<DashboardCard[]>) {
    const sameColumn = event.previousContainer.data === event.container.data;
    const sameRow = event.previousIndex === event.currentIndex;
    if (!sameColumn || !sameRow) {
      if (sameColumn) {
        moveItemInArray(
          event.container.data,
          event.previousIndex,
          event.currentIndex,
        );
      } else {
        transferArrayItem(
          event.previousContainer.data,
          event.container.data,
          event.previousIndex,
          event.currentIndex,
        );
      }
      this.saveCards();
      this.updateWidth();
    }
  }

  hint(): void {
    this.editmode = true;
    setTimeout(() => {
      this.menuTrigger.openMenu();
    }, 666);
  }

  add(card: DashboardCard): void {
    const position = this.addCard(card);
    this.saveCard(card, position.column, position.row);
    this.updateWidth();
    this.updateAvailable();
  }

  delete(card: DashboardCard): void {
    for (const column of this.grid) {
      for (let row = 0; row < column.length; row++) {
        if (column[row].id === card.id) {
          column.splice(row, 1);
          this.deleteCard(card);
          this.updateWidth();
          this.updateAvailable();
          return;
        }
      }
    }
  }

  private saveCards(): void {
    this.gebruikersvoorkeurenService
      .updateDashboardCards(this.getInstellingen())
      .subscribe((dashboardInstellingen) => {
        this.instellingen = dashboardInstellingen;
      });
  }

  private saveCard(card: DashboardCard, column: number, row: number): void {
    this.gebruikersvoorkeurenService
      .addDashboardCard(this.getInstellingAt(card, column, row))
      .subscribe((dashboardInstellingen) => {
        this.instellingen = dashboardInstellingen;
      });
  }

  private deleteCard(card: DashboardCard): void {
    this.gebruikersvoorkeurenService
      .deleteDashboardCard(this.getInstelling(card))
      .subscribe((dashboardInstellingen) => {
        this.instellingen = dashboardInstellingen;
      });
  }

  private getInstellingen(): DashboardCardInstelling[] {
    const instellingen: DashboardCardInstelling[] = [];
    for (let column = 0; column < this.grid.length; column++) {
      const rows = this.grid[column];
      for (let row = 0; row < rows.length; row++) {
        instellingen.push(this.getInstellingAt(rows[row], column, row));
      }
    }
    return instellingen;
  }

  private getInstellingAt(
    card: DashboardCard,
    column: number,
    row: number,
  ): DashboardCardInstelling {
    const instelling = this.getInstelling(card);
    instelling.column = column;
    instelling.row = row;
    return instelling;
  }

  private getInstelling(card: DashboardCard): DashboardCardInstelling {
    for (const existingInstelling of this.instellingen) {
      if (existingInstelling.cardId === card.id) {
        existingInstelling.signaleringType = card.signaleringType;
        return existingInstelling;
      }
    }
    const newInstelling: DashboardCardInstelling =
      new DashboardCardInstelling();
    newInstelling.cardId = card.id;
    newInstelling.signaleringType = card.signaleringType;
    return newInstelling;
  }
}

class Position {
  constructor(
    public column: number,
    public row: number,
  ) {}
}
