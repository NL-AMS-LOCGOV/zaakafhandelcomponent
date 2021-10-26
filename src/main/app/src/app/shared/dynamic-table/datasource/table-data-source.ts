/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, merge, Observable, Subscription} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {tap} from 'rxjs/operators';
import {TableRequest} from './table-request';
import {TableResponse} from './table-response';
import {TableColumn} from '../column/table-column';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {TableColumnFilter} from '../filter/table-column-filter';
import {DynamicPipe} from '../pipes/dynamic.pipe';

export abstract class TableDataSource<OBJECT> extends DataSource<OBJECT> {

    private tableSubject = new BehaviorSubject<OBJECT[]>([]);
    public totalItems: number = 0;

    paginator: MatPaginator;
    sort: MatSort;
    filters: {} = {};

    columns: Array<TableColumn>;
    selectedColumns: Array<TableColumn>;

    private subscription$: Subscription;

    connect(collectionViewer: CollectionViewer): Observable<OBJECT[] | ReadonlyArray<OBJECT>> {
        //reset pageindex on sort change
        this.subscription$ = this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);
        //watch sortchange and page change
        merge(this.sort.sortChange, this.paginator.page).pipe(
            tap(() => this.load())
        ).subscribe();

        return this.tableSubject.asObservable();
    }

    filter(filter: TableColumnFilter<any>) {
        if (this.filters[filter.id] && !filter.value) {
            delete this.filters[filter.id];
        } else {
            this.filters[filter.id] = filter.value[filter.selectValue];
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
        moveItemInArray(this.columns, event.previousIndex, event.currentIndex);
    }

    updateColumns(): void {
        //update array reference for angular change detection
        this.columns.filter(value => !value.sticky).forEach(value => value.visible = this.selectedColumns.indexOf(value) != -1);
    }

    protected setData(response: TableResponse<OBJECT>): void {
        this.totalItems = response.totalItems;
        this.applyValuePipes(response.data);
        this.tableSubject.next(response.data);
    }

    private applyValuePipes(data: OBJECT[]): void {
        const dynamicPipe = new DynamicPipe();

        this.columns.forEach(column => {
            var model = column.model.split('.');
            data.forEach(dataObj => {
                let i = 0, value, obj = dataObj;
                while (i < model.length) {
                    if (obj) {
                        obj = obj.hasOwnProperty(model[i]) ? obj[model[i]] : null;
                    }
                    i++;
                }
                value = obj;
                if (column.pipe) {
                    dataObj[column.model] = dynamicPipe.transform(value, column.pipe, column.pipeArg);
                } else {
                    dataObj[column.model] = value;
                }
            });
        });
    }

    public setViewChilds(paginator: MatPaginator, sort: MatSort): void {
        this.paginator = paginator;
        this.sort = sort;
    }
}
