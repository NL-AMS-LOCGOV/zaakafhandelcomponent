/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {ZakenWerkvoorraadDatasource} from './zaken-werkvoorraad-datasource';
import {MatButtonToggle} from '@angular/material/button-toggle';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {Zaaktype} from '../model/zaaktype';
import {IdentityService} from '../../identity/identity.service';
import {Groep} from '../../identity/model/groep';
import {detailExpand} from '../../shared/animations/animations';
import {SelectionModel} from '@angular/cdk/collections';
import {ZakenVerdelenDialogComponent} from '../zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {ZakenVrijgevenDialogComponent} from '../zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component';
import {Medewerker} from '../../identity/model/medewerker';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {TextIcon} from '../../shared/edit/text-icon';
import {SessionStorageService} from '../../shared/storage/session-storage.service';

@Component({
    templateUrl: './zaken-werkvoorraad.component.html',
    styleUrls: ['./zaken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class ZakenWerkvoorraadComponent implements AfterViewInit, OnInit, OnDestroy {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakOverzicht>;
    dataSource: ZakenWerkvoorraadDatasource;
    selection = new SelectionModel<ZaakOverzicht>(true, []);

    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;

    expandedRow: ZaakOverzicht | null;

    groepen: Groep[] = [];
    zaakTypes: Zaaktype[] = [];
    private ingelogdeMedewerker: Medewerker;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService, public utilService: UtilService,
                private identityService: IdentityService, public dialog: MatDialog,
                private sessionStorageService: SessionStorageService) { }

    ngOnInit() {
        this.utilService.setTitle('title.zaken.werkvoorraad');
        this.getIngelogdeMedewerker();
        this.dataSource = new ZakenWerkvoorraadDatasource(this.zakenService, this.utilService);
        this.dataSource.initColumns({
            select: ColumnPickerValue.STICKY,
            identificatie: ColumnPickerValue.VISIBLE,
            status: ColumnPickerValue.VISIBLE,
            zaaktype: ColumnPickerValue.VISIBLE,
            groep: ColumnPickerValue.VISIBLE,
            startdatum: ColumnPickerValue.VISIBLE,
            einddatum: ColumnPickerValue.HIDDEN,
            einddatumGepland: ColumnPickerValue.HIDDEN,
            aanvrager: ColumnPickerValue.VISIBLE,
            behandelaar: ColumnPickerValue.VISIBLE,
            uiterlijkeEinddatumAfdoening: ColumnPickerValue.HIDDEN,
            toelichting: ColumnPickerValue.HIDDEN,
            url: ColumnPickerValue.STICKY
        });

        this.zaaktypesOphalen();
        this.groepenOphalen();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    ngOnDestroy() {
        const werklijstData = {
            searchParameters: this.dataSource.zoekParameters,
            filters: this.dataSource.filters,
            columns: {
                allColumns: this.dataSource.columns,
                visibleColumns: this.dataSource.visibleColumns,
                selectedColumns: this.dataSource.selectedColumns,
                detailExpandColumns: this.dataSource.detailExpandColumns
            },
            sorting: {
                column: this.sort.active,
                direction: this.sort.direction
            },
            paginator: {
                page: this.paginator.pageIndex,
                pageSize: this.paginator.pageSize
            }
        };

        this.sessionStorageService.setSessionStorage('zakenWerkvoorraadData', werklijstData);
    }

    private zaaktypesOphalen() {
        this.zakenService.listZaaktypes().subscribe(zaakTypes => {
            this.zaakTypes = zaakTypes;
        });
    }

    private groepenOphalen() {
        this.identityService.listGroepen().subscribe(groepen => {
            this.groepen = groepen;
        });
    }

    private getIngelogdeMedewerker() {
        this.identityService.readIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    switchTypeAndSearch() {
        if (this.dataSource.zoekParameters[this.dataSource.zoekParameters.selectie]) {
            this.zoekZaken();
        }
    }

    zoekZaken() {
        this.dataSource.zoekZaken();
        this.selection.clear();
        this.paginator.firstPage();
    }

    assignToMe(zaakOverzicht: ZaakOverzicht, $event) {
        $event.stopPropagation();

        this.zakenService.toekennenAanIngelogdeMedewerkerVanuitLijst(zaakOverzicht).subscribe(zaak => {
            zaakOverzicht.behandelaar = zaak.behandelaar;
            zaakOverzicht['behandelaar.naam'] = zaak.behandelaar.naam;
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar.naam});
        });
    }

    showAssignToMe(row: ZaakOverzicht): boolean {
        return this.ingelogdeMedewerker.gebruikersnaam !== row.behandelaar?.gebruikersnaam;
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
    checkboxLabel(row?: ZaakOverzicht): string {
        if (!row) {
            return `actie.alles.${this.isAllSelected() ? 'deselecteren' : 'selecteren'}`;
        }

        return `actie.${this.selection.isSelected(row) ? 'deselecteren' : 'selecteren'}`;
    }

    pageChange(): void {
        this.selection.clear();
    }

    openVerdelenScherm(): void {
        const zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVerdelenDialogComponent, {
            width: '300px',
            data: zaken,
            autoFocus: 'dialog'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.verdeeld.zaak');
                } else {
                    this.utilService.openSnackbar('msg.verdeeld.zaken', {aantal: this.selection.selected.length});
                }
                this.zoekZaken();
            }
        });
    }

    openVrijgevenScherm(): void {
        const zaken = this.selection.selected;
        const dialogRef = this.dialog.open(ZakenVrijgevenDialogComponent, {
            width: '350px',
            data: zaken,
            autoFocus: 'dialog'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.vrijgegeven.zaak');
                } else {
                    this.utilService.openSnackbar('msg.vrijgegeven.zaken', {aantal: this.selection.selected.length});
                }
                this.zoekZaken();
            }
        });
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }
}
