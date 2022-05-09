/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {ZakenWerkvoorraadDatasource} from './zaken-werkvoorraad-datasource';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {Zaaktype} from '../model/zaaktype';
import {IdentityService} from '../../identity/identity.service';
import {Group} from '../../identity/model/group';
import {detailExpand} from '../../shared/animations/animations';
import {SelectionModel} from '@angular/cdk/collections';
import {ZakenVerdelenDialogComponent} from '../zaken-verdelen-dialog/zaken-verdelen-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {ZakenVrijgevenDialogComponent} from '../zaken-vrijgeven-dialog/zaken-vrijgeven-dialog.component';
import {User} from '../../identity/model/user';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {TextIcon} from '../../shared/edit/text-icon';
import {WerklijstData} from '../../shared/dynamic-table/model/werklijst-data';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';

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

    expandedRow: ZaakOverzicht | null;

    groepen: Group[] = [];
    zaakTypes: Zaaktype[] = [];
    private ingelogdeMedewerker: User;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    werklijstData: WerklijstData;

    constructor(private zakenService: ZakenService, public utilService: UtilService,
                private identityService: IdentityService, public dialog: MatDialog,
                private cd: ChangeDetectorRef) { }

    ngOnInit() {
        this.utilService.setTitle('title.zaken.werkvoorraad');
        this.getIngelogdeMedewerker();
        this.dataSource = new ZakenWerkvoorraadDatasource(this.zakenService, this.utilService);

        this.zaaktypesOphalen();
        this.groepenOphalen();

        this.werklijstData = SessionStorageUtil.getItem('zakenWerkvoorraadData') as WerklijstData;

        if (this.werklijstData) {
            this.dataSource.zoekParameters = this.werklijstData.searchParameters;
        }

        this.setColumns();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;

        if (this.werklijstData) {
            this.dataSource.filters = this.werklijstData.filters;

            this.paginator.pageIndex = this.werklijstData.paginator.page;
            this.paginator.pageSize = this.werklijstData.paginator.pageSize;

            this.sort.active = this.werklijstData.sorting.column;
            this.sort.direction = this.werklijstData.sorting.direction;

            // Manually trigger ChangeDetection because changes have been made to the sort
            this.cd.detectChanges();

            if (this.dataSource.zoekParameters.groep !== null || this.dataSource.zoekParameters.zaaktype !== null) {
                this.searchCases();
            }
        }
    }

    ngOnDestroy() {
        this.saveSearchQuery();
    }

    private zaaktypesOphalen() {
        this.zakenService.listZaaktypes().subscribe(zaakTypes => {
            this.zaakTypes = zaakTypes;
        });
    }

    private groepenOphalen() {
        this.identityService.listGroups().subscribe(groepen => {
            this.groepen = groepen;
        });
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    private setColumns() {
        if (this.werklijstData) {
            const mapColumns: Map<string, ColumnPickerValue> = new Map(JSON.parse(this.werklijstData.columns));
            this.toggleGroepOrZaaktype(mapColumns);
        } else {
            this.toggleGroepOrZaaktype(this.initialColumns());
        }
    }

    initialColumns(): Map<string, ColumnPickerValue> {
        return new Map([
            ['select', ColumnPickerValue.STICKY],
            ['zaak.identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaaktype', ColumnPickerValue.HIDDEN],
            ['omschrijving', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['openstaandeTaken', ColumnPickerValue.VISIBLE],
            ['einddatum', ColumnPickerValue.HIDDEN],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['dagenTotStreefdatum', ColumnPickerValue.HIDDEN],
            ['behandelaar', ColumnPickerValue.VISIBLE],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['dagenTotFataledatum', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    toggleGroepOrZaaktype(columns: Map<string, ColumnPickerValue>): Map<string, ColumnPickerValue> {
        if (this.dataSource.zoekParameters.selectie === 'groep') {
            columns.set('groep', ColumnPickerValue.HIDDEN);
            columns.set('zaaktype', ColumnPickerValue.VISIBLE);
        } else {
            columns.set('groep', ColumnPickerValue.VISIBLE);
            columns.set('zaaktype', ColumnPickerValue.HIDDEN);
        }

        this.dataSource.initColumns(columns);

        return columns;
    }

    switchTypeAndSearch() {
        this.setColumns();
        if (this.dataSource.zoekParameters[this.dataSource.zoekParameters.selectie]) {
            this.searchAndGoToFirstPage();
        }
    }

    searchAndGoToFirstPage() {
        this.searchCases();
        this.paginator.firstPage();
    }

    searchCases() {
        this.dataSource.zoekZaken();
        this.selection.clear();
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
        return this.ingelogdeMedewerker.id !== row.behandelaar?.id;
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
            data: zaken
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.verdeeld.zaak');
                } else {
                    this.utilService.openSnackbar('msg.verdeeld.zaken', {aantal: this.selection.selected.length});
                }
                this.searchAndGoToFirstPage();
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
                this.searchAndGoToFirstPage();
            }
        });
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    saveSearchQuery() {
        const flatListColumns = JSON.stringify([...this.dataSource.columns]);
        const werklijstData = new WerklijstData();
        werklijstData.searchParameters = this.dataSource.zoekParameters;
        werklijstData.filters = this.dataSource.filters;
        werklijstData.columns = flatListColumns;
        werklijstData.sorting = {
            column: this.sort.active,
            direction: this.sort.direction
        };
        werklijstData.paginator = {
            page: this.paginator.pageIndex,
            pageSize: this.paginator.pageSize
        };

        SessionStorageUtil.setItem('zakenWerkvoorraadData', werklijstData);
    }

    resetSearchCriteria() {
        this.dataSource.zoekParameters = {
            selectie: 'groep',
            groep: null,
            zaaktype: null
        };
        this.dataSource.filters = {};
        this.dataSource.initColumns(this.initialColumns());
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = 25;
        this.sort.active = '';
        this.sort.direction = '';

        this.saveSearchQuery();

        this.dataSource.clear();
    }

}
