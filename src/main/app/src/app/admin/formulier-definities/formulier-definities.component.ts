/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit, ViewChild } from "@angular/core";
import { UtilService } from "../../core/service/util.service";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { IdentityService } from "../../identity/identity.service";
import { MatTableDataSource } from "@angular/material/table";
import { AdminComponent } from "../admin/admin.component";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../../shared/confirm-dialog/confirm-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { FormulierDefinitieService } from "../formulier-defintie.service";
import { FormulierDefinitie } from "../model/formulieren/formulier-definitie";

@Component({
  templateUrl: "./formulier-definities.component.html",
  styleUrls: ["./formulier-definities.component.less"],
})
export class FormulierDefinitiesComponent
  extends AdminComponent
  implements OnInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  isLoadingResults = false;
  columns: string[] = [
    "systeemnaam",
    "naam",
    "beschrijving",
    "creatiedatum",
    "wijzigingsdatum",
    "aantal",
    "id",
  ];
  dataSource: MatTableDataSource<FormulierDefinitie> =
    new MatTableDataSource<FormulierDefinitie>();

  constructor(
    private identityService: IdentityService,
    private service: FormulierDefinitieService,
    public dialog: MatDialog,
    public utilService: UtilService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.setupMenu("title.formulierdefinities");
    this.ophalenFormulierDefinities();
  }

  ophalenFormulierDefinities(): void {
    this.isLoadingResults = true;
    this.utilService.setLoading(true);
    this.service.list().subscribe((definities) => {
      this.dataSource.data = definities;
      this.isLoadingResults = false;
      this.utilService.setLoading(false);
    });
  }

  verwijderen(formulierDefinitie: FormulierDefinitie): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: new ConfirmDialogData(
          {
            key: "msg.formulier.definitie.verwijderen.bevestigen",
            args: { naam: formulierDefinitie.systeemnaam },
          },
          this.service.delete(formulierDefinitie.id),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar(
            "msg.formulier.definitie.verwijderen.uitgevoerd",
            { naam: formulierDefinitie.systeemnaam },
          );
          this.ophalenFormulierDefinities();
        }
      });
  }
}
