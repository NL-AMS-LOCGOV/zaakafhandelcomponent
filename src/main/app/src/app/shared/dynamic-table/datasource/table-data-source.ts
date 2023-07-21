/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { DataSource } from "@angular/cdk/collections";
import { BehaviorSubject, merge, Observable, Subscription } from "rxjs";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { tap } from "rxjs/operators";
import { TableRequest } from "./table-request";
import { TableResponse } from "./table-response";
import { CdkDragDrop, moveItemInArray } from "@angular/cdk/drag-drop";
import { ColumnPickerValue } from "../column-picker/column-picker-value";

export abstract class TableDataSource<OBJECT> extends DataSource<OBJECT> {
  private tableSubject = new BehaviorSubject<OBJECT[]>([]);
  public totalItems = 0;

  paginator: MatPaginator;
  sort: MatSort;
  filters: {} = {};

  private _columns: Map<string, ColumnPickerValue>;
  private _visibleColumns: Array<string>;
  private _filterColumns: Array<string>;
  private _detailExpandColumns: Array<string>;

  private subscription$: Subscription;

  connect(): Observable<OBJECT[] | ReadonlyArray<OBJECT>> {
    // reset pageindex on sort change
    this.subscription$ = this.sort.sortChange.subscribe(
      () => (this.paginator.pageIndex = 0),
    );
    // watch sortchange and page change
    merge(this.sort.sortChange, this.paginator.page)
      .pipe(tap(() => this.load()))
      .subscribe();

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
  disconnect(): void {
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

  protected getTableRequest(): TableRequest {
    return {
      pagination: {
        pageNumber: this.paginator?.pageIndex,
        pageSize: this.paginator?.pageSize,
      },
      sort: {
        predicate: this.sort?.direction ? this.sort?.active : null,
        direction: this.sort?.direction,
      },
      search: {
        predicateObject: this.filters,
      },
    };
  }

  drop(event: CdkDragDrop<string[]>) {
    const extraIndex = this.visibleColumns.includes("select") ? 1 : 0;
    moveItemInArray(
      this.visibleColumns,
      event.previousIndex + extraIndex,
      event.currentIndex + extraIndex,
    );
  }

  protected setData(response: TableResponse<OBJECT>): void {
    this.totalItems = response.totalItems;
    this.tableSubject.next(response.data);
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
    this._visibleColumns = [...columns.keys()].filter(
      (key) => columns.get(key) !== ColumnPickerValue.HIDDEN,
    );
    this._detailExpandColumns = [...columns.keys()].filter(
      (key) => columns.get(key) === ColumnPickerValue.HIDDEN,
    );
    this._filterColumns = this.visibleColumns.map((c) => c + "_filter");
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
