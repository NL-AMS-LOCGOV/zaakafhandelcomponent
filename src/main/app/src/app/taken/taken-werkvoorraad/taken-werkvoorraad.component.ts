/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Taak} from '../model/taak';
import {TakenWerkvoorraadDatasource} from './taken-werkvoorraad-datasource';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {UtilService} from '../../core/service/util.service';
import {detailExpand} from '../../shared/animations/animations';
import {SelectionModel} from '@angular/cdk/collections';
import {MatDialog} from '@angular/material/dialog';
import {TakenVerdelenDialogComponent} from '../taken-verdelen-dialog/taken-verdelen-dialog.component';
import {TakenVrijgevenDialogComponent} from '../taken-vrijgeven-dialog/taken-vrijgeven-dialog.component';
import {IdentityService} from '../../identity/identity.service';
import {User} from '../../identity/model/user';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {TextIcon} from '../../shared/edit/text-icon';
import {WerklijstData} from '../../shared/dynamic-table/model/werklijstdata';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';

@Component({
    templateUrl: './taken-werkvoorraad.component.html',
    styleUrls: ['./taken-werkvoorraad.component.less'],
    animations: [detailExpand]
})
export class TakenWerkvoorraadComponent implements AfterViewInit, OnInit, OnDestroy {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<Taak>;

    dataSource: TakenWerkvoorraadDatasource;
    expandedRow: Taak | null;
    selection = new SelectionModel<Taak>(true, []);
    private ingelogdeMedewerker: User;
    streefdatumIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');

    werklijstData: WerklijstData;

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private identityService: IdentityService, public dialog: MatDialog,
                private cd: ChangeDetectorRef) {
    }

    ngOnInit() {
        this.utilService.setTitle('title.taken.werkvoorraad');
        this.getIngelogdeMedewerker();
        this.dataSource = new TakenWerkvoorraadDatasource(this.takenService, this.utilService);

        this.werklijstData = SessionStorageUtil.getItem('takenWerkvoorraadData') as WerklijstData;

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
        }

        this.dataSource.load();
    }

    ngOnDestroy() {
        this.saveSearchQuery();
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    showAssignToMe(taak: Taak): boolean {
        return this.ingelogdeMedewerker.id !== taak.behandelaar?.id;
    }

    assignToMe(taak: Taak, event) {
        event.stopPropagation();
        this.takenService.assignToLoggedOnUser(taak).subscribe(returnTaak => {
            taak['behandelaar'] = returnTaak.behandelaar;
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.behandelaar.naam});
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
    checkboxLabel(row?: Taak): string {
        if (!row) {
            return `actie.alles.${this.isAllSelected() ? 'deselecteren' : 'selecteren'}`;
        }

        return `actie.${this.selection.isSelected(row) ? 'deselecteren' : 'selecteren'}`;
    }

    isSelected(): boolean {
        return this.selection.selected.length > 0;
    }

    countSelected(): number {
        return this.selection.selected.length;
    }

    openVerdelenScherm() {
        const taken = this.selection.selected;
        const dialogRef = this.dialog.open(TakenVerdelenDialogComponent, {
            data: taken
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.verdeeld.taak');
                } else {
                    this.utilService.openSnackbar('msg.verdeeld.taken', {aantal: this.selection.selected.length});
                }
                this.findTaken();
            }
        });
    }

    openVrijgevenScherm() {
        const taken = this.selection.selected;
        const dialogRef = this.dialog.open(TakenVrijgevenDialogComponent, {
            data: taken
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (this.selection.selected.length === 1) {
                    this.utilService.openSnackbar('msg.vrijgegeven.taak');
                } else {
                    this.utilService.openSnackbar('msg.vrijgegeven.taken', {aantal: this.selection.selected.length});
                }
                this.findTaken();
            }
        });
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    private setColumns() {
        if (this.werklijstData) {
            const mapColumns: Map<string, ColumnPickerValue> = new Map(JSON.parse(this.werklijstData.columns));
            this.dataSource.initColumns(mapColumns);
        } else {
            this.dataSource.initColumns(this.initialColumns());
        }
    }

    initialColumns(): Map<string, ColumnPickerValue> {
        return new Map([
            ['select', ColumnPickerValue.STICKY],
            ['naam', ColumnPickerValue.VISIBLE],
            ['zaakIdentificatie', ColumnPickerValue.VISIBLE],
            ['zaaktypeOmschrijving', ColumnPickerValue.VISIBLE],
            ['creatiedatumTijd', ColumnPickerValue.VISIBLE],
            ['streefdatum', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    saveSearchQuery() {
        const flatListColumns = JSON.stringify([...this.dataSource.columns]);
        const werklijstData = new WerklijstData();
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

        SessionStorageUtil.setItem('takenWerkvoorraadData', werklijstData);
    }

    resetSearchCriteria() {
        this.dataSource.filters = {};
        this.dataSource.initColumns(this.initialColumns());
        this.paginator.pageSize = 25;
        this.sort.active = '';
        this.sort.direction = '';

        this.saveSearchQuery();
        this.findTaken();
    }

    private findTaken() {
        this.dataSource.load();
        this.selection.clear();
        this.paginator.firstPage();
    }
}
