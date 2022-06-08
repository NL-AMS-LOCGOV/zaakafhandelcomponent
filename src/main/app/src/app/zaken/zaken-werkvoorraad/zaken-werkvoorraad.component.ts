/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';

import {detailExpand} from '../../shared/animations/animations';

import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {UtilService} from '../../core/service/util.service';
import {SelectionModel} from '@angular/cdk/collections';
import {ZakenService} from '../zaken.service';
import {IdentityService} from '../../identity/identity.service';
import {MatDialog} from '@angular/material/dialog';
import {MatTable} from '@angular/material/table';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {User} from '../../identity/model/user';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';

import {ZakenVerdelenDialogComponent} from '../zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {ZakenVrijgevenDialogComponent} from '../zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component';
import {ZakenWerkvoorraadZoekenDatasource} from './zaken-werkvorraad-datasource';

@Component({
    selector: 'zac-zaken-werkvoorraad',
    templateUrl: './zaken-werkvoorraad.component.html',
    styleUrls: ['./zaken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class ZakenWerkvoorraadComponent implements AfterViewInit, OnInit, OnDestroy {

    selection = new SelectionModel<ZaakZoekObject>(true, []);
    dataSource: ZakenWerkvoorraadZoekenDatasource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakZoekObject>;
    defaults: ZoekParameters;
    ingelogdeMedewerker: User;
    expandedRow: ZaakZoekObject | null;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService,
                private zoekenService: ZoekenService,
                public utilService: UtilService,
                private identityService: IdentityService, public dialog: MatDialog) {
        this.dataSource = new ZakenWerkvoorraadZoekenDatasource(this.zoekenService, this.utilService);
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.werkvoorraad');
        this.getIngelogdeMedewerker();
        this.dataSource.initColumns(this.defaultColumns());
    }

    defaultColumns(): Map<string, ColumnPickerValue> {
        return new Map([
            ['select', ColumnPickerValue.STICKY],
            ['zaak.identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['omschrijving', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['openstaandeTaken', ColumnPickerValue.HIDDEN],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['dagenTotStreefdatum', ColumnPickerValue.HIDDEN],
            ['behandelaar', ColumnPickerValue.VISIBLE],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['dagenTotFataledatum', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
        this.dataSource.load();
    }

    ngOnDestroy(): void {
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
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
            return `actie.alles.${this.isAllSelected() ? 'deselecteren' : 'selecteren'}`;
        }

        return `actie.${this.selection.isSelected(row) ? 'deselecteren' : 'selecteren'}`;
    }

    pageChange(): void {
        this.selection.clear();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    resetSearch(): void {
        this.dataSource.reset();
    }

    resetColumns(): void {
        this.dataSource.resetColumns();
    }

    filtersChange(): void {
        this.paginator.pageIndex = 0;
        this.selection.clear();
        this.dataSource.load();
    }

    clearDate($event: MouseEvent, datum: string): void {
        $event.stopPropagation();
        this.dataSource.zoekParameters.datums[datum].van = null;
        this.dataSource.zoekParameters.datums[datum].tot = null;
        this.filtersChange();
    }

    hasDate(datum: string): boolean {
        return this.dataSource.zoekParameters.datums[datum].van != null;
    }

    assignToMe(zaakZoekObject: ZaakZoekObject, $event) {
        $event.stopPropagation();

        this.zakenService.toekennenAanIngelogdeMedewerkerVanuitLijst(zaakZoekObject).subscribe(zaak => {
            zaakZoekObject.behandelaarNaam = zaak.behandelaar.naam;
            zaakZoekObject.behandelaarGebruikersnaam = zaak.behandelaar.id;
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar.naam});
        });
    }

    showAssignToMe(row: ZaakZoekObject): boolean {
        return this.ingelogdeMedewerker && this.ingelogdeMedewerker.id !== row.behandelaarGebruikersnaam;
    }

    openVerdelenScherm(): void {
        const zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVerdelenDialogComponent, {
            data: zaken
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.verdeeld.zaak');
                } else {
                    this.utilService.openSnackbar('msg.verdeeld.zaken', {aantal: this.selection.selected.length});
                }
                this.filtersChange();
            }
        });
    }

    openVrijgevenScherm(): void {
        const zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVrijgevenDialogComponent, {
            data: zaken
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.vrijgegeven.zaak');
                } else {
                    this.utilService.openSnackbar('msg.vrijgegeven.zaken', {aantal: this.selection.selected.length});
                }
                this.filtersChange();
            }
        });
    }

    getFilters(field): string[] {
        if (this.dataSource.beschikbareFilters[field]) {
            return this.dataSource.beschikbareFilters[field].sort((a, b) => a.localeCompare(b));
        }
    }
}
