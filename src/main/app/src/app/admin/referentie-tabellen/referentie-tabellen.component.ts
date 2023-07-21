/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit, ViewChild } from "@angular/core";
import { UtilService } from "../../core/service/util.service";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { IdentityService } from "../../identity/identity.service";
import { MatTableDataSource } from "@angular/material/table";
import { AdminComponent } from "../admin/admin.component";
import { ReferentieTabelService } from "../referentie-tabel.service";
import { ReferentieTabel } from "../model/referentie-tabel";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../../shared/confirm-dialog/confirm-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { TranslateService } from "@ngx-translate/core";

@Component({
  templateUrl: "./referentie-tabellen.component.html",
  styleUrls: ["./referentie-tabellen.component.less"],
})
export class ReferentieTabellenComponent
  extends AdminComponent
  implements OnInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  isLoadingResults = false;
  columns: string[] = ["code", "systeem", "naam", "waarden", "id"];
  dataSource: MatTableDataSource<ReferentieTabel> =
    new MatTableDataSource<ReferentieTabel>();

  constructor(
    private identityService: IdentityService,
    private service: ReferentieTabelService,
    public dialog: MatDialog,
    private translate: TranslateService,
    public utilService: UtilService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.setupMenu("title.referentietabellen");
    this.laadReferentieTabellen();
  }

  laadReferentieTabellen(): void {
    this.isLoadingResults = true;
    this.service.listReferentieTabellen().subscribe((tabellen) => {
      this.dataSource.data = tabellen;
      this.isLoadingResults = false;
    });
  }

  verwijderReferentieTabel(referentieTabel: ReferentieTabel): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: new ConfirmDialogData(
          {
            key: "msg.tabel.verwijderen.bevestigen",
            args: { tabel: referentieTabel.code },
          },
          this.service.deleteReferentieTabel(referentieTabel.id),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar("msg.tabel.verwijderen.uitgevoerd", {
            tabel: referentieTabel.code,
          });
          this.laadReferentieTabellen();
        }
      });
  }
}
