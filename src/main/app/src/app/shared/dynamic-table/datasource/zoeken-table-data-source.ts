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

export abstract class ZoekenTableDataSource<OBJECT> extends DataSource<OBJECT> {

    beschikbareFilters: { [key: string]: string } = {};
    totalItems: number = 0;
    paginator: MatPaginator;
    sort: MatSort;

    private tableSubject = new BehaviorSubject<OBJECT[]>([]);
    private _columns: Map<string, ColumnPickerValue>;
    private _visibleColumns: Array<string>;
    private _filterColumns: Array<string>;
    private _detailExpandColumns: Array<string>;

    private subscription$: Subscription;

    connect(collectionViewer: CollectionViewer): Observable<OBJECT[] | ReadonlyArray<OBJECT>> {
        // reset pageindex on sort change
        this.subscription$ = this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);
        // watch sortchange and page change
        merge(this.sort.sortChange, this.paginator.page).pipe(
            tap(() => this.load())
        ).subscribe();

        return this.tableSubject.asObservable();
    }

    /**
     *  Called when the table is being destroyed. Use this function, to clean up
     * any open connections or free any held resources that were set up during connect.
     */
    disconnect(collectionViewer: CollectionViewer): void {
        if (this.subscription$) {
            this.subscription$.unsubscribe();
        }
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
     * @param columns available columns
     */
    initColumns(columns: Map<string, ColumnPickerValue>): void {
        this._columns = columns;
        this.updateColumns(columns);
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
