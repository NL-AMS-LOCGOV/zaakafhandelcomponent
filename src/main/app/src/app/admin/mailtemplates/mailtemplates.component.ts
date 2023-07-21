/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { AdminComponent } from "../admin/admin.component";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { MatTableDataSource } from "@angular/material/table";
import { Mailtemplate } from "../model/mailtemplate";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../../shared/confirm-dialog/confirm-dialog.component";
import { IdentityService } from "../../identity/identity.service";
import { MatDialog } from "@angular/material/dialog";
import { TranslateService } from "@ngx-translate/core";
import { UtilService } from "../../core/service/util.service";
import { MailtemplateBeheerService } from "../mailtemplate-beheer.service";
import { MailtemplateKoppelingService } from "../mailtemplate-koppeling.service";
import { MailtemplateKoppeling } from "../model/mailtemplate-koppeling";
import { forkJoin } from "rxjs";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { Sort } from "@angular/material/sort";

@Component({
  templateUrl: "./mailtemplates.component.html",
  styleUrls: ["./mailtemplates.component.less"],
  animations: [
    trigger("detailExpand", [
      state("collapsed", style({ height: "0px", minHeight: "0" })),
      state("expanded", style({ height: "*" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"),
      ),
    ]),
  ],
})
export class MailtemplatesComponent
  extends AdminComponent
  implements OnInit, AfterViewInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  isLoadingResults = false;
  columns: string[] = ["mailTemplateNaam", "mail", "defaultMailtemplate", "id"];
  columnsToDisplay = [
    "expand",
    "mailTemplateNaam",
    "mail",
    "defaultMailtemplate",
    "id",
  ];
  dataSource: MatTableDataSource<Mailtemplate> =
    new MatTableDataSource<Mailtemplate>();
  mailKoppelingen: MailtemplateKoppeling[];
  filterValue = "";
  expandedRow: Mailtemplate | null;

  constructor(
    private identityService: IdentityService,
    private mailtemplateBeheerService: MailtemplateBeheerService,
    public dialog: MatDialog,
    private translate: TranslateService,
    public utilService: UtilService,
    private mailtemplateKoppelingService: MailtemplateKoppelingService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.setupMenu("title.mailtemplates");
    this.laadMailtemplates();
  }

  laadMailtemplates(): void {
    this.isLoadingResults = true;
    forkJoin([
      this.mailtemplateBeheerService.listMailtemplates(),
      this.mailtemplateKoppelingService.listMailtemplateKoppelingen(),
    ]).subscribe(([mailtemplates, koppelingen]) => {
      this.dataSource.data = mailtemplates;
      this.mailKoppelingen = koppelingen;
      this.isLoadingResults = false;
    });
  }

  isDisabled(mailtemplate: Mailtemplate): boolean {
    return this.getMailtemplateKoppeling(mailtemplate) != null;
  }

  getDisabledTitle(mailtemplate: Mailtemplate): string {
    return this.translate.instant("msg.mailtemplate.verwijderen.disabled", {
      zaaktype:
        this.getMailtemplateKoppeling(mailtemplate).zaakafhandelParameters
          .zaaktype.omschrijving,
    });
  }

  verwijderMailtemplate(mailtemplate: Mailtemplate): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: new ConfirmDialogData(
          "msg.mailtemplate.verwijderen.bevestigen",
          this.mailtemplateBeheerService.deleteMailtemplate(mailtemplate.id),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar(
            "msg.mailtemplate.verwijderen.uitgevoerd",
          );
          this.laadMailtemplates();
        }
      });
  }

  getKoppelingen(mailtemplate: Mailtemplate) {
    const koppelingen: MailtemplateKoppeling[] = [];
    this.mailKoppelingen.forEach((koppeling) => {
      if (koppeling.mailtemplate.id === mailtemplate.id) {
        koppelingen.push(koppeling);
      }
    });

    return koppelingen;
  }

  private getMailtemplateKoppeling(mailtemplate: Mailtemplate) {
    return this.mailKoppelingen.find(
      (koppeling) => koppeling.mailtemplate.id === mailtemplate.id,
    );
  }

  applyFilter(event?: Event) {
    if (event) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.filterValue = filterValue.trim().toLowerCase();
      this.dataSource.filter = filterValue;
    } else {
      // toggleSwitch
      this.dataSource.filter = " " + this.filterValue;
    }
  }

  sortData(sort: Sort) {
    if (!sort.active || sort.direction === "") {
      return;
    }

    this.dataSource.data = this.dataSource.data.slice().sort((a, b) => {
      const isAsc = sort.direction === "asc";
      switch (sort.active) {
        case "mail":
          return this.compare(a.mail, b.mail, isAsc);
        case "mailTemplateNaam":
          return this.compare(a.mailTemplateNaam, b.mailTemplateNaam, isAsc);
        default:
          return 0;
      }
    });
  }

  compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }
}
