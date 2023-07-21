/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { UtilService } from "../../core/service/util.service";
import { InboxDocumentenService } from "../inbox-documenten.service";
import {
  AfterViewInit,
  Component,
  EventEmitter,
  OnInit,
  ViewChild,
} from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { merge } from "rxjs";
import { map, startWith, switchMap } from "rxjs/operators";
import { InformatieObjectenService } from "../../informatie-objecten/informatie-objecten.service";
import { MatTableDataSource } from "@angular/material/table";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../../shared/confirm-dialog/confirm-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { TranslateService } from "@ngx-translate/core";
import { InboxDocument } from "../model/inbox-document";
import { InformatieObjectVerplaatsService } from "../../informatie-objecten/informatie-object-verplaats.service";
import { InboxDocumentListParameters } from "../model/inbox-document-list-parameters";
import { Zoekopdracht } from "../../gebruikersvoorkeuren/model/zoekopdracht";
import { Werklijst } from "../../gebruikersvoorkeuren/model/werklijst";
import { SessionStorageUtil } from "../../shared/storage/session-storage.util";
import { WerklijstComponent } from "../../shared/dynamic-table/datasource/werklijst-component";
import { GebruikersvoorkeurenService } from "../../gebruikersvoorkeuren/gebruikersvoorkeuren.service";
import { ActivatedRoute } from "@angular/router";

@Component({
  templateUrl: "./inbox-documenten-list.component.html",
  styleUrls: ["./inbox-documenten-list.component.less"],
})
export class InboxDocumentenListComponent
  extends WerklijstComponent
  implements OnInit, AfterViewInit
{
  isLoadingResults = true;
  dataSource: MatTableDataSource<InboxDocument> =
    new MatTableDataSource<InboxDocument>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  displayedColumns: string[] = [
    "enkelvoudiginformatieobjectID",
    "creatiedatum",
    "titel",
    "actions",
  ];
  filterColumns: string[] = [
    "identificatie_filter",
    "creatiedatum_filter",
    "titel_filter",
    "actions_filter",
  ];
  listParameters: InboxDocumentListParameters;
  filterChange: EventEmitter<void> = new EventEmitter<void>();
  clearZoekopdracht: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    private inboxDocumentenService: InboxDocumentenService,
    private infoService: InformatieObjectenService,
    private utilService: UtilService,
    public dialog: MatDialog,
    private translate: TranslateService,
    private informatieObjectVerplaatsService: InformatieObjectVerplaatsService,
    public gebruikersvoorkeurenService: GebruikersvoorkeurenService,
    public route: ActivatedRoute,
  ) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.listParameters = SessionStorageUtil.getItem(
      Werklijst.INBOX_DOCUMENTEN + "_ZOEKPARAMETERS",
      this.createDefaultParameters(),
    );
    this.utilService.setTitle("title.documenten.inboxDocumenten");
  }

  ngAfterViewInit(): void {
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
    merge(this.sort.sortChange, this.paginator.page, this.filterChange)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          this.utilService.setLoading(true);
          this.updateListParameters();
          return this.inboxDocumentenService.list(this.listParameters);
        }),
        map((data) => {
          this.isLoadingResults = false;
          this.utilService.setLoading(false);
          return data;
        }),
      )
      .subscribe((data) => {
        this.paginator.length = data.totaal;
        this.dataSource.data = data.resultaten;
      });
  }

  updateListParameters(): void {
    this.listParameters.sort = this.sort.active;
    this.listParameters.order = this.sort.direction;
    this.listParameters.page = this.paginator.pageIndex;
    this.listParameters.maxResults = this.paginator.pageSize;
    SessionStorageUtil.setItem(
      Werklijst.INBOX_DOCUMENTEN + "_ZOEKPARAMETERS",
      this.listParameters,
    );
  }

  getDownloadURL(id: InboxDocument): string {
    return this.infoService.getDownloadURL(id.enkelvoudiginformatieobjectUUID);
  }

  documentVerplaatsen(id: InboxDocument): void {
    id["disabled"] = true;
    this.infoService
      .readEnkelvoudigInformatieobject(id.enkelvoudiginformatieobjectUUID)
      .subscribe((i) => {
        this.informatieObjectVerplaatsService.addTeVerplaatsenDocument(
          i,
          "inbox-documenten",
        );
      });
  }

  documentVerwijderen(inboxDocument: InboxDocument): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: new ConfirmDialogData(
          {
            key: "msg.document.verwijderen.bevestigen",
            args: { document: inboxDocument.titel },
          },
          this.inboxDocumentenService.delete(inboxDocument),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar("msg.document.verwijderen.uitgevoerd", {
            document: inboxDocument.titel,
          });
          this.paginator.page.emit();
        }
      });
  }

  isDocumentVerplaatsenDisabled(inboxDocument: InboxDocument): boolean {
    return (
      inboxDocument["disabled"] ||
      this.informatieObjectVerplaatsService.isReedsTeVerplaatsen(
        inboxDocument.enkelvoudiginformatieobjectUUID,
      )
    );
  }
  filtersChanged(): void {
    this.paginator.pageIndex = 0;
    this.clearZoekopdracht.emit();
    this.filterChange.emit();
  }

  resetSearch(): void {
    this.listParameters = SessionStorageUtil.setItem(
      Werklijst.INBOX_DOCUMENTEN + "_ZOEKPARAMETERS",
      this.createDefaultParameters(),
    );
    this.sort.active = this.listParameters.sort;
    this.sort.direction = this.listParameters.order;
    this.paginator.pageIndex = 0;
    this.filterChange.emit();
  }

  createDefaultParameters(): InboxDocumentListParameters {
    return new InboxDocumentListParameters("creatiedatum", "desc");
  }

  zoekopdrachtChanged(actieveZoekopdracht: Zoekopdracht): void {
    if (actieveZoekopdracht) {
      this.listParameters = JSON.parse(actieveZoekopdracht.json);
      this.sort.active = this.listParameters.sort;
      this.sort.direction = this.listParameters.order;
      this.paginator.pageIndex = 0;
      this.filterChange.emit();
    } else if (actieveZoekopdracht === null) {
      this.resetSearch();
    } else {
      this.filterChange.emit();
    }
  }

  getWerklijst(): Werklijst {
    return Werklijst.INBOX_DOCUMENTEN;
  }
}
