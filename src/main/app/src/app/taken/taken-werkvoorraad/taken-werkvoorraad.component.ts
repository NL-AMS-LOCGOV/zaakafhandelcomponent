/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";

import { detailExpand } from "../../shared/animations/animations";

import { ColumnPickerValue } from "../../shared/dynamic-table/column-picker/column-picker-value";
import { UtilService } from "../../core/service/util.service";
import { SelectionModel } from "@angular/cdk/collections";
import { IdentityService } from "../../identity/identity.service";
import { MatDialog } from "@angular/material/dialog";
import { MatTable } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { ZoekenService } from "../../zoeken/zoeken.service";
import { LoggedInUser } from "../../identity/model/logged-in-user";
import { TextIcon } from "../../shared/edit/text-icon";
import { Conditionals } from "../../shared/edit/conditional-fn";
import { SorteerVeld } from "src/app/zoeken/model/sorteer-veld";
import { TaakZoekObject } from "../../zoeken/model/taken/taak-zoek-object";
import { TakenWerkvoorraadDatasource } from "./taken-werkvoorraad-datasource";
import { TakenService } from "../taken.service";
import { ActivatedRoute } from "@angular/router";
import { TakenVerdelenDialogComponent } from "../taken-verdelen-dialog/taken-verdelen-dialog.component";
import { TakenVrijgevenDialogComponent } from "../taken-vrijgeven-dialog/taken-vrijgeven-dialog.component";
import { ZoekenColumn } from "../../shared/dynamic-table/model/zoeken-column";
import { GebruikersvoorkeurenService } from "../../gebruikersvoorkeuren/gebruikersvoorkeuren.service";
import { WerklijstComponent } from "../../shared/dynamic-table/datasource/werklijst-component";
import { Werklijst } from "../../gebruikersvoorkeuren/model/werklijst";

@Component({
  templateUrl: "./taken-werkvoorraad.component.html",
  styleUrls: ["./taken-werkvoorraad.component.less"],
  animations: [detailExpand],
})
export class TakenWerkvoorraadComponent
  extends WerklijstComponent
  implements AfterViewInit, OnInit
{
  selection = new SelectionModel<TaakZoekObject>(true, []);
  dataSource: TakenWerkvoorraadDatasource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<TaakZoekObject>;
  ingelogdeMedewerker: LoggedInUser;
  expandedRow: TaakZoekObject | null;
  readonly zoekenColumn = ZoekenColumn;
  sorteerVeld = SorteerVeld;

  fataledatumIcon: TextIcon = new TextIcon(
    Conditionals.isAfterDate(),
    "report_problem",
    "warningVerlopen_icon",
    "msg.datum.overschreden",
    "error",
  );

  constructor(
    public route: ActivatedRoute,
    private takenService: TakenService,
    public utilService: UtilService,
    private identityService: IdentityService,
    public dialog: MatDialog,
    private zoekenService: ZoekenService,
    public gebruikersvoorkeurenService: GebruikersvoorkeurenService,
  ) {
    super();
    this.dataSource = new TakenWerkvoorraadDatasource(
      this.zoekenService,
      this.utilService,
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.utilService.setTitle("title.taken.werkvoorraad");
    this.getIngelogdeMedewerker();
    this.dataSource.initColumns(this.defaultColumns());
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

  showAssignToMe(taakZoekObject: TaakZoekObject): boolean {
    return (
      taakZoekObject.rechten.toekennen &&
      this.ingelogdeMedewerker &&
      this.ingelogdeMedewerker.id !==
        taakZoekObject.behandelaarGebruikersnaam &&
      this.ingelogdeMedewerker.groupIds.indexOf(taakZoekObject.groepID) >= 0
    );
  }

  assignToMe(taakZoekObject: TaakZoekObject, event): void {
    event.stopPropagation();
    this.takenService
      .toekennenAanIngelogdeMedewerkerVanuitLijst(taakZoekObject)
      .subscribe((returnTaak) => {
        taakZoekObject.behandelaarNaam = returnTaak.behandelaar.naam;
        taakZoekObject.behandelaarGebruikersnaam = returnTaak.behandelaar.id;
        this.utilService.openSnackbar("msg.taak.toegekend", {
          behandelaar: returnTaak.behandelaar.naam,
        });
      });
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
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
  checkboxLabel(row?: TaakZoekObject): string {
    if (!row) {
      return `actie.alles.${
        this.isAllSelected() ? "deselecteren" : "selecteren"
      }`;
    }

    return `actie.${
      this.selection.isSelected(row) ? "deselecteren" : "selecteren"
    }`;
  }

  isSelected(): boolean {
    return this.selection.selected.length > 0;
  }

  countSelected(): number {
    return this.selection.selected.length;
  }

  openVerdelenScherm(): void {
    const taken = this.selection.selected;
    const dialogRef = this.dialog.open(TakenVerdelenDialogComponent, {
      data: taken,
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
    const taken = this.selection.selected;
    const dialogRef = this.dialog.open(TakenVrijgevenDialogComponent, {
      data: taken,
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (this.selection.selected.length === 1) {
          this.utilService.openSnackbar("msg.vrijgegeven.taak");
        } else {
          this.utilService.openSnackbar("msg.vrijgegeven.taken", {
            aantal: this.selection.selected.length,
          });
        }
        this.filtersChange();
      }
    });
  }

  isAfterDate(datum): boolean {
    return Conditionals.isOverschreden(datum);
  }

  defaultColumns(): Map<ZoekenColumn, ColumnPickerValue> {
    const columns = new Map([
      [ZoekenColumn.SELECT, ColumnPickerValue.STICKY],
      [ZoekenColumn.NAAM, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.ZAAK_IDENTIFICATIE, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.ZAAK_OMSCHRIJVING, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.ZAAK_TOELICHTING, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.ZAAKTYPE_OMSCHRIJVING, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.CREATIEDATUM, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.FATALEDATUM, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.DAGEN_TOT_FATALEDATUM, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.GROEP, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.BEHANDELAAR, ColumnPickerValue.VISIBLE],
      [ZoekenColumn.TOELICHTING, ColumnPickerValue.HIDDEN],
      [ZoekenColumn.URL, ColumnPickerValue.STICKY],
    ]);
    if (!this.werklijstRechten.zakenTakenVerdelen) {
      columns.delete(ZoekenColumn.SELECT);
    }
    return columns;
  }

  getWerklijst(): Werklijst {
    return Werklijst.WERKVOORRAAD_TAKEN;
  }

  paginatorChanged($event: PageEvent): void {
    super.paginatorChanged($event);
    this.selection.clear();
  }

  resetSearch(): void {
    this.dataSource.reset();
  }

  resetColumns(): void {
    this.dataSource.resetColumns();
  }

  filtersChange(): void {
    this.selection.clear();
    this.dataSource.filtersChanged();
  }
}
