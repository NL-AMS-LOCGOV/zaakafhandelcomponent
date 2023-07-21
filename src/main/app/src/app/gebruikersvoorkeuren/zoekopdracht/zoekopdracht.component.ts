/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { GebruikersvoorkeurenService } from "../gebruikersvoorkeuren.service";
import { Zoekopdracht } from "../model/zoekopdracht";
import { Werklijst } from "../model/werklijst";
import { Subscription } from "rxjs";
import { ZoekopdrachtSaveDialogComponent } from "../zoekopdracht-save-dialog/zoekopdracht-save-dialog.component";
import { ZoekParameters } from "../../zoeken/model/zoek-parameters";
import { OntkoppeldDocumentListParameters } from "../../documenten/model/ontkoppeld-document-list-parameters";
import { InboxDocumentListParameters } from "../../documenten/model/inbox-document-list-parameters";

@Component({
  selector: "zac-zoekopdracht",
  templateUrl: "./zoekopdracht.component.html",
  styleUrls: ["./zoekopdracht.component.less"],
})
export class ZoekopdrachtComponent implements OnInit, OnDestroy {
  @Input() werklijst: Werklijst;
  @Input() zoekFilters: ZoekFilters;
  @Output() zoekopdracht = new EventEmitter<Zoekopdracht>();
  @Input() filtersChanged: EventEmitter<void>;

  zoekopdrachten: Zoekopdracht[] = [];
  actieveZoekopdracht: Zoekopdracht;
  actieveFilters: boolean;
  filtersChangedSubscription$: Subscription;

  constructor(
    private gebruikersvoorkeurenService: GebruikersvoorkeurenService,
    private dialog: MatDialog,
  ) {}

  ngOnDestroy(): void {
    this.filtersChangedSubscription$.unsubscribe();
  }

  ngOnInit(): void {
    this.loadZoekopdrachten();
    this.filtersChangedSubscription$ = this.filtersChanged.subscribe(() => {
      this.clearActief();
    });
  }

  saveSearch(): void {
    const dialogRef = this.dialog.open(ZoekopdrachtSaveDialogComponent, {
      data: {
        zoekopdrachten: this.zoekopdrachten,
        lijstID: this.werklijst,
        zoekopdracht: this.zoekFilters,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadZoekopdrachten();
      }
    });
  }

  setActief(zoekopdracht: Zoekopdracht): void {
    this.actieveZoekopdracht = zoekopdracht;
    this.actieveFilters = true;
    this.zoekopdracht.emit(this.actieveZoekopdracht);
    this.gebruikersvoorkeurenService
      .setZoekopdrachtActief(this.actieveZoekopdracht)
      .subscribe();
  }

  deleteZoekopdracht($event: MouseEvent, zoekopdracht: Zoekopdracht): void {
    $event.stopPropagation();
    this.gebruikersvoorkeurenService
      .deleteZoekOpdrachten(zoekopdracht.id)
      .subscribe(() => {
        this.loadZoekopdrachten();
      });
  }

  clearActief(emit?: boolean): void {
    this.actieveZoekopdracht = null;
    this.gebruikersvoorkeurenService
      .removeZoekopdrachtActief(this.werklijst)
      .subscribe();
    if (emit) {
      this.actieveFilters = false;
      this.zoekopdracht.emit(this.actieveZoekopdracht);
    } else {
      this.actieveFilters = this.heeftActieveFilters();
    }
  }

  private loadZoekopdrachten(): void {
    this.gebruikersvoorkeurenService
      .listZoekOpdrachten(this.werklijst)
      .subscribe((zoekopdrachten) => {
        this.zoekopdrachten = zoekopdrachten;
        this.actieveZoekopdracht = zoekopdrachten.find((z) => z.actief);
        this.actieveFilters = this.heeftActieveFilters();
        this.zoekopdracht.emit(this.actieveZoekopdracht);
      });
  }

  // Need to DIY the OO here, because typescript manages to lose the prototype :-(
  private heeftActieveFilters(): boolean {
    switch (this.zoekFilters.filtersType) {
      case "ZoekParameters":
        return ZoekParameters.heeftActieveFilters(this.zoekFilters);
      case "OntkoppeldDocumentListParameters":
        return OntkoppeldDocumentListParameters.heeftActieveFilters(
          this.zoekFilters,
        );
      case "InboxDocumentListParameters":
        return InboxDocumentListParameters.heeftActieveFilters(
          this.zoekFilters,
        );
      default:
        return false;
    }
  }
}

export interface ZoekFilters {
  readonly filtersType: string;
}
