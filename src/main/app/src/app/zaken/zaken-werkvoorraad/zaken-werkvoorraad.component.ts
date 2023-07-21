/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";

import { detailExpand } from "../../shared/animations/animations";

import { ColumnPickerValue } from "../../shared/dynamic-table/column-picker/column-picker-value";
import { UtilService } from "../../core/service/util.service";
import { SelectionModel } from "@angular/cdk/collections";
import { ZakenService } from "../zaken.service";
import { IdentityService } from "../../identity/identity.service";
import { MatDialog } from "@angular/material/dialog";
import { MatTable } from "@angular/material/table";
import { ZaakZoekObject } from "../../zoeken/model/zaken/zaak-zoek-object";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { ZoekenService } from "../../zoeken/zoeken.service";
import { LoggedInUser } from "../../identity/model/logged-in-user";
import { TextIcon } from "../../shared/edit/text-icon";
import { Conditionals } from "../../shared/edit/conditional-fn";

import { ZakenVerdelenDialogComponent } from "../zaken-verdelen-dialog/zaken-verdelen-dialog.component";
import { ZakenVrijgevenDialogComponent } from "../zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component";
import { ZakenWerkvoorraadDatasource } from "./zaken-werkvoorraad-datasource";
import { SorteerVeld } from "src/app/zoeken/model/sorteer-veld";
import { ZoekenColumn } from "../../shared/dynamic-table/model/zoeken-column";
import { IndicatiesLayout } from "../../shared/indicaties/indicaties.component";
import { GebruikersvoorkeurenService } from "../../gebruikersvoorkeuren/gebruikersvoorkeuren.service";
import { WerklijstComponent } from "../../shared/dynamic-table/datasource/werklijst-component";
import { Werklijst } from "../../gebruikersvoorkeuren/model/werklijst";
import { ActivatedRoute } from "@angular/router";

@Component({
  templateUrl: "./zaken-werkvoorraad.component.html",
  styleUrls: ["./zaken-werkvoorraad.component.less"],
  animations: [detailExpand],
})
export class ZakenWerkvoorraadComponent
  extends WerklijstComponent
  implements AfterViewInit, OnInit
{
  readonly indicatiesLayout = IndicatiesLayout;
  selection = new SelectionModel<ZaakZoekObject>(true, []);
  dataSource: ZakenWerkvoorraadDatasource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<ZaakZoekObject>;
  ingelogdeMedewerker: LoggedInUser;
  expandedRow: ZaakZoekObject | null;
  readonly zoekenColumn = ZoekenColumn;
  sorteerVeld = SorteerVeld;

  einddatumGeplandIcon: TextIcon = new TextIcon(
    Conditionals.isAfterDate(),
    "report_problem",
    "warningVerlopen_icon",
    "msg.datum.overschreden",
    "warning",
  );
  uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(
    Conditionals.isAfterDate(),
    "report_problem",
    "errorVerlopen_icon",
    "msg.datum.overschreden",
    "error",
  );

  constructor(
    private zakenService: ZakenService,
    public gebruikersvoorkeurenService: GebruikersvoorkeurenService,
    public route: ActivatedRoute,
    private zoekenService: ZoekenService,
    public utilService: UtilService,
    public dialog: MatDialog,
    private identityService: IdentityService,
  ) {
    super();
    this.dataSource = new ZakenWerkvoorraadDatasource(
      this.zoekenService,
      this.utilService,
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.utilService.setTitle("title.zaken.werkvoorraad");
    this.getIngelogdeMedewerker();
    this.dataSource.initColumns(this.defaultColumns());
  }

  defaultColumns(): Map<ZoekenColumn, ColumnPickerValue> {
    const columns = new Map([
      [ZoekenColumn.SELECT, ColumnPickerValue.STICKY],
      [ZoekenColumn.ZAAK_DOT_IDENTIFICATIE, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.STATUS, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.ZAAKTYPE, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.OMSCHRIJVING, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.GROEP, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.COMMUNICATIEKANAAL, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.VERTROUWELIJKHEIDAANDUIDING, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.STARTDATUM, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.REGISTRATIEDATUM, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.OPENSTAANDE_TAKEN, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.EINDDATUM_GEPLAND, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.DAGEN_TOT_STREEFDATUM, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.BEHANDELAAR, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.UITERLIJKE_EINDDATUM_AFDOENING, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.DAGEN_TOT_FATALEDATUM, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.TOELICHTING, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.INDICATIES, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.URL, ColumnPickerValue.STICKY],
    ]);
    if (!this.werklijstRechten.zakenTakenVerdelen) {
      columns.delete(ZoekenColumn.SELECT);
    }
    return columns;
  }

  getWerklijst(): Werklijst {
    return Werklijst.WERKVOORRAAD_ZAKEN;
  }

  ngAfterViewInit(): void {
    this.dataSource.setViewChilds(this.paginator, this.sort);
    this.table.dataSource = this.dataSource;
  }

  private getIngelogdeMedewerker() {
    this.identityService.readLoggedInUser().subscribe((ingelogdeMedewerker) => {
      this.ingelogdeMedewerker = ingelogdeMedewerker;
    });
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  isSelected() {
    return this.selection.selected.length > 0;
  }

  countSelected() {
    return this.selection.selected.length;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: ZaakZoekObject): string {
    if (!row) {
      return `actie.alles.${
        this.isAllSelected() ? "deselecteren" : "selecteren"
      }`;
    }

    return `actie.${
      this.selection.isSelected(row) ? "deselecteren" : "selecteren"
    }`;
  }

  paginatorChanged($event: PageEvent): void {
    super.paginatorChanged($event);
    this.selection.clear();
  }

  isAfterDate(datum): boolean {
    return Conditionals.isOverschreden(datum);
  }

  resetColumns(): void {
    this.dataSource.resetColumns();
  }

  filtersChange(): void {
    this.selection.clear();
    this.dataSource.filtersChanged();
  }

  assignToMe(zaakZoekObject: ZaakZoekObject, $event) {
    $event.stopPropagation();

    this.zakenService
      .toekennenAanIngelogdeMedewerkerVanuitLijst(zaakZoekObject)
      .subscribe((zaak) => {
        zaakZoekObject.behandelaarNaam = zaak.behandelaar.naam;
        zaakZoekObject.behandelaarGebruikersnaam = zaak.behandelaar.id;
        this.utilService.openSnackbar("msg.zaak.toegekend", {
          behandelaar: zaak.behandelaar.naam,
        });
      });
  }

  showAssignToMe(zaakZoekObject: ZaakZoekObject): boolean {
    return (
      zaakZoekObject.rechten.toekennen &&
      this.ingelogdeMedewerker &&
      this.ingelogdeMedewerker.id !==
        zaakZoekObject.behandelaarGebruikersnaam &&
      this.ingelogdeMedewerker.groupIds.indexOf(zaakZoekObject.groepId) >= 0
    );
  }

  openVerdelenScherm(): void {
    const zaken = this.selection.selected;
    const dialogRef = this.dialog.open(ZakenVerdelenDialogComponent, {
      data: zaken,
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (this.selection.selected.length === 1) {
          this.utilService.openSnackbar("msg.verdeeld.zaak");
        } else {
          this.utilService.openSnackbar("msg.verdeeld.zaken", {
            aantal: this.selection.selected.length,
          });
        }
        this.filtersChange();
      }
    });
  }

  openVrijgevenScherm(): void {
    const zaken = this.selection.selected;
    const dialogRef = this.dialog.open(ZakenVrijgevenDialogComponent, {
      data: zaken,
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (this.selection.selected.length === 1) {
          this.utilService.openSnackbar("msg.vrijgegeven.zaak");
        } else {
          this.utilService.openSnackbar("msg.vrijgegeven.zaken", {
            aantal: this.selection.selected.length,
          });
        }
        this.filtersChange();
      }
    });
  }
}
