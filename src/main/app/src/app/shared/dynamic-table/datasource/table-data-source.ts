/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, merge, Observable, Subscription} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {tap} from 'rxjs/operators';
import {TableRequest} from './table-request';
import {TableResponse} from './table-response';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';

export abstract class TableDataSource<OBJECT> extends DataSource<OBJECT> {

    private tableSubject = new BehaviorSubject<OBJECT[]>([]);
    public totalItems: number = 0;

    paginator: MatPaginator;
    sort: MatSort;
    filters: {} = {};

    columns: Array<string>;
    visibleColumns: Array<string>;
    filterColumns: Array<string>;
    detailExpandColumns: Array<string>;
    selectedColumns: Array<string>;

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

    filter(field, value) {
        if (this.filters[field] && !value) {
            delete this.filters[field];
        } else {
            this.filters[field] = value;
        }
        this.load();
    }

    hasFilter() {
        return Object.keys(this.filters).length;
    }

    setFilter(key: string, value: string) {
        this.filters[key] = value;
        this.load();
    }

    removeFilter(key: string) {
        if (this.filters[key]) {
            delete this.filters[key];
        }
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

    abstract load();

    protected getTableRequest(): TableRequest {
        return {
            pagination: {
                pageNumber: this.paginator?.pageIndex,
                pageSize: this.paginator?.pageSize
            },
            sort: {
                predicate: this.sort?.direction ? this.sort?.active : null,
                direction: this.sort?.direction
            },
            search: {
                predicateObject: this.filters
            }
        };
    }

    drop(event: CdkDragDrop<string[]>) {
        const extraIndex = this.visibleColumns.includes('select') ? 1 : 0;
        moveItemInArray(this.visibleColumns, event.previousIndex + extraIndex, event.currentIndex + extraIndex);
    }

    setFilterColumns(): void {
        this.visibleColumns = this.selectedColumns;
        this.filterColumns = this.visibleColumns.map(c => c + '_filter');

        this.restoreUrlColumn();
        this.restoreSelectColumn();
    }

    private restoreUrlColumn() {
        if (this.columns.includes('url') && !this.visibleColumns.includes('url')) {
            this.visibleColumns.push('url');
            this.filterColumns.push('url_filter');
        }
    }

    private restoreSelectColumn() {
        if (this.columns.includes('select') && !this.visibleColumns.includes('select')) {
            this.visibleColumns.unshift('select');
            this.filterColumns.unshift('select_filter');
        }
    }

    protected setData(response: TableResponse<OBJECT>): void {
        this.totalItems = response.totalItems;
        this.tableSubject.next(response.data);
    }

    private getValue(columnModel: string, row: OBJECT): OBJECT {
        const model = columnModel.split('.');
        let i = 0, value = row;
        while (i < model.length) {
            if (value) {
                value = value.hasOwnProperty(model[i]) ? value[model[i]] : null;
            }
            i++;
        }
        return value;
    }

    public setViewChilds(paginator: MatPaginator, sort: MatSort): void {
        this.paginator = paginator;
        this.sort = sort;
    }
}
