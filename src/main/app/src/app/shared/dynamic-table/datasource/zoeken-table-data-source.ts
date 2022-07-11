/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, merge, Observable, Subscription} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {finalize, tap} from 'rxjs/operators';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {ColumnPickerValue} from '../column-picker/column-picker-value';
import {ZoekResultaat} from '../../../zoeken/model/zoek-resultaat';
import {SessionStorageUtil} from '../../storage/session-storage.util';
import {ZoekParameters} from '../../../zoeken/model/zoek-parameters';
import {ZoekenService} from '../../../zoeken/zoeken.service';
import {UtilService} from '../../../core/service/util.service';
import {ZoekObject} from '../../../zoeken/model/zoek-object';
import {Werklijst} from '../../../gebruikersvoorkeuren/model/werklijst';
import {GebruikersvoorkeurenService} from '../../../gebruikersvoorkeuren/gebruikersvoorkeuren.service';
import {ZoekopdrachtSaveDialogComponent} from '../../../gebruikersvoorkeuren/zoekopdracht-save-dialog/zoekopdracht-save-dialog.component';
import {Zoekopdracht} from '../../../gebruikersvoorkeuren/model/zoekopdracht';
import {MatDialog} from '@angular/material/dialog';

export abstract class ZoekenTableDataSource<OBJECT extends ZoekObject> extends DataSource<OBJECT> {

    zoekParameters: ZoekParameters;
    beschikbareFilters: { [key: string]: string[] } = {};
    totalItems: number = 0;
    paginator: MatPaginator;
    sort: MatSort;

    zoekopdrachten: Zoekopdracht[] = [];
    actieveZoekopdracht: Zoekopdracht;
    ready = false;

    private tableSubject = new BehaviorSubject<ZoekObject[]>([]);
    private _defaultColumns: Map<string, ColumnPickerValue>;
    private _columns: Map<string, ColumnPickerValue>;
    private _sessionKey: string;
    private _visibleColumns: Array<string>;
    private _filterColumns: Array<string>;
    private _detailExpandColumns: Array<string>;
    private subscriptions$: Subscription[] = [];

    protected constructor(private werklijst: Werklijst,
                          private zoekenService: ZoekenService,
                          private gebruikersvoorkeurenService: GebruikersvoorkeurenService,
                          public dialog: MatDialog,
                          private utilService: UtilService) {
        super();
        this.zoekParameters = new ZoekParameters();
        this.loadZoekopdrachten();
    }

    protected abstract initZoekparameters(zoekParameters: ZoekParameters): void;

    private updateZoekParameters(): ZoekParameters {
        this.initZoekparameters(this.zoekParameters);
        this.zoekParameters.page = this.paginator.pageIndex;
        this.zoekParameters.rows = this.paginator.pageSize;
        this.zoekParameters.sorteerRichting = this.sort.direction;
        this.zoekParameters.sorteerVeld = this.sort.active;
        return this.zoekParameters;
    }

    connect(collectionViewer: CollectionViewer): Observable<OBJECT[] | ReadonlyArray<OBJECT>> {
        this.subscriptions$.push(this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0));
        this.subscriptions$.push(merge(this.sort.sortChange, this.paginator.page).pipe(
            tap(() => this.load())
        ).subscribe());
        return this.tableSubject.asObservable() as Observable<OBJECT[]>;
    }

    /**
     *  Called when the table is being destroyed. Use this function, to clean up
     * any open connections or free any held resources that were set up during connect.
     */
    disconnect(collectionViewer: CollectionViewer): void {
        this.subscriptions$.forEach(s => { s.unsubscribe(); });
        this.tableSubject.complete();
    }

    load(): void {
        this.utilService.setLoading(true);
        this.zoekenService.list(this.updateZoekParameters())
            .pipe(
                finalize(() => this.utilService.setLoading(false))
            ).subscribe(zaakResponse => {
                this.setData(zaakResponse);
            }
        );
    }

    clear() {
        this.totalItems = 0;
        this.tableSubject.next([]);
    }

    drop(event: CdkDragDrop<string[]>) {
        const extraIndex = this.visibleColumns.includes('select') ? 1 : 0;
        moveItemInArray(this.visibleColumns, event.previousIndex + extraIndex, event.currentIndex + extraIndex);
        moveItemInArray(this.filterColumns, event.previousIndex + extraIndex, event.currentIndex + extraIndex);
    }

    setData(response: ZoekResultaat<ZoekObject>): void {
        this.totalItems = response.totaal;
        this.tableSubject.next(response.resultaten);
        this.beschikbareFilters = response.filters;
    }

    setViewChilds(paginator: MatPaginator, sort: MatSort): void {
        this.paginator = paginator;
        this.sort = sort;
    }

    /**
     * Columns can only be instantiated with the initColumns method
     *
     * @param defaultColumns available columns
     */
    initColumns(defaultColumns: Map<string, ColumnPickerValue>): void {
        const key = this.werklijst + 'Columns';
        const columnsString = JSON.stringify(Array.from(defaultColumns.entries()));
        const sessionColumns: string = SessionStorageUtil.getItem(key, columnsString);
        const columns: Map<string, ColumnPickerValue> = new Map(JSON.parse(sessionColumns));
        this._defaultColumns = defaultColumns;
        this._columns = columns;
        this._sessionKey = key;
        this.updateColumns(columns);
    }

    resetColumns() {
        this._columns = new Map(this._defaultColumns);
        this.updateColumns(this._defaultColumns);
    }

    /**
     * Update column visibility
     *
     * @param columns updated columns
     */
    updateColumns(columns: Map<string, ColumnPickerValue>): void {
        this._visibleColumns = [...columns.keys()].filter(key => columns.get(key) !== ColumnPickerValue.HIDDEN);
        this._detailExpandColumns = [...columns.keys()].filter(key => columns.get(key) === ColumnPickerValue.HIDDEN);
        this._filterColumns = this.visibleColumns.map(c => c + '_filter');
        this.storeColumns(columns);
    }

    reset() {
        this.zoekParameters = new ZoekParameters();
        this.sort.active = this.zoekParameters.sorteerVeld;
        this.sort.direction = this.zoekParameters.sorteerRichting;
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = this.zoekParameters.rows;
        this.load();
    }

    private storeColumns(columns: Map<string, ColumnPickerValue>): void {
        const columnsString = JSON.stringify(Array.from(columns.entries()));
        SessionStorageUtil.setItem(this._sessionKey, columnsString);
    }

    /* column getters, NO setters!*/
    get columns(): Map<string, ColumnPickerValue> {
        return this._columns;
    }

    get visibleColumns(): Array<string> {
        return this._visibleColumns;
    }

    get detailExpandColumns(): Array<string> {
        return this._detailExpandColumns;
    }

    get filterColumns(): Array<string> {
        return this._filterColumns;
    }

    get data(): OBJECT[] {
        return this.tableSubject.value as OBJECT[];
    }

    saveSearch(): void {
        const dialogRef = this.dialog.open(ZoekopdrachtSaveDialogComponent, {
            data: {zoekopdrachten: this.zoekopdrachten, lijstID: this.werklijst, zoekopdracht: this.zoekParameters}
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadZoekopdrachten();
            }
        });
    }

    loadZoekopdrachten(): void {
        console.log('loadZoekopdrachten');
        this.gebruikersvoorkeurenService.listZoekOpdrachten(this.werklijst).subscribe(zoekopdrachten => {
            this.zoekopdrachten = zoekopdrachten;
            this.actieveZoekopdracht = zoekopdrachten.find(z => z.actief);
            if (this.actieveZoekopdracht) {
                this.zoekParameters = JSON.parse(this.actieveZoekopdracht.json);
                this.load();
            } else {
                console.log('reset');
                this.reset();
            }
        });
    }

    setActief(zoekopdracht: Zoekopdracht): void {
        this.actieveZoekopdracht = zoekopdracht;
        this.zoekParameters = JSON.parse(this.actieveZoekopdracht.json);
        this.load();
        this.gebruikersvoorkeurenService.setZoekopdrachtActief(this.actieveZoekopdracht).subscribe();
    }

    deleteZoekopdracht($event: MouseEvent, zoekopdracht: Zoekopdracht): void {
        $event.stopPropagation();
        this.gebruikersvoorkeurenService.deleteZoekOpdrachten(zoekopdracht.id).subscribe(() => {
            this.loadZoekopdrachten();
        });
    }

    removeActief(): void {
        this.actieveZoekopdracht = null;
        this.gebruikersvoorkeurenService.removeZoekopdrachtActief(this.werklijst).subscribe();
        this.reset();
    }
}
