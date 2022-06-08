/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, merge, Observable, Subscription} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {tap} from 'rxjs/operators';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {ColumnPickerValue} from '../column-picker/column-picker-value';
import {ZoekResultaat} from '../../../zoeken/model/zoek-resultaat';
import {SessionStorageUtil} from '../../storage/session-storage.util';

export abstract class ZoekenTableDataSource<OBJECT> extends DataSource<OBJECT> {

    beschikbareFilters: { [key: string]: string[] } = {};
    totalItems: number = 0;
    paginator: MatPaginator;
    sort: MatSort;

    private tableSubject = new BehaviorSubject<OBJECT[]>([]);
    private _defaultColumns: Map<string, ColumnPickerValue>;
    private _columns: Map<string, ColumnPickerValue>;
    private _sessionKey: string;
    private _visibleColumns: Array<string>;
    private _filterColumns: Array<string>;
    private _detailExpandColumns: Array<string>;

    private subscriptions$: Subscription[] = [];

    connect(collectionViewer: CollectionViewer): Observable<OBJECT[] | ReadonlyArray<OBJECT>> {
        this.subscriptions$.push(this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0));
        this.subscriptions$.push(merge(this.sort.sortChange, this.paginator.page).pipe(
            tap(() => this.load())
        ).subscribe());
        return this.tableSubject.asObservable();
    }

    /**
     *  Called when the table is being destroyed. Use this function, to clean up
     * any open connections or free any held resources that were set up during connect.
     */
    disconnect(collectionViewer: CollectionViewer): void {
        this.subscriptions$.forEach(s => { s.unsubscribe(); });
        this.tableSubject.complete();
    }

    get data(): OBJECT[] {
        return this.tableSubject.value;
    }

    clear() {
        this.totalItems = 0;
        this.tableSubject.next([]);
    }

    abstract load();

    drop(event: CdkDragDrop<string[]>) {
        const extraIndex = this.visibleColumns.includes('select') ? 1 : 0;
        moveItemInArray(this.visibleColumns, event.previousIndex + extraIndex, event.currentIndex + extraIndex);
        moveItemInArray(this.filterColumns, event.previousIndex + extraIndex, event.currentIndex + extraIndex);
    }

    protected setData(response: ZoekResultaat<OBJECT>): void {
        this.totalItems = response.totaal;
        this.tableSubject.next(response.resultaten);
        this.beschikbareFilters = response.filters;
    }

    public setViewChilds(paginator: MatPaginator, sort: MatSort): void {
        this.paginator = paginator;
        this.sort = sort;
    }

    /**
     * Columns can only be instantiated with the initColumns method
     *
     * @param defaultColumns available columns
     * @param key sessionKey
     */
    protected _initColumns(key: string, defaultColumns: Map<string, ColumnPickerValue>): void {
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
}
