/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DashboardCard} from '../model/dashboard-card';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {interval, Observable, Subscription} from 'rxjs';

@Component({
    template: '',
    styleUrls: ['./dashboard-card.component.less']
})
export abstract class DashboardCardComponent<T> implements OnInit, AfterViewInit, OnDestroy {

    private readonly RELOAD_INTERVAL: number = 5 * 60 * 1000; // 5 min.

    @Input() data: DashboardCard;

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    dataSource: MatTableDataSource<T> = new MatTableDataSource<T>();

    protected reload: Observable<any>;
    private reloader: Subscription;

    abstract columns: string[];

    constructor() { }

    ngOnInit(): void {
        this.onLoad(this.afterLoad);
    }

    ngAfterViewInit(): void {
        if (this.reload == null) {
            this.reload = interval(this.RELOAD_INTERVAL);
        }
        this.reloader = this.reload.subscribe(next => {
            this.onLoad(this.afterLoad);
        });
    }

    ngOnDestroy(): void {
        this.reloader.unsubscribe();
    }

    protected abstract onLoad(afterLoad: () => void): void;

    private readonly afterLoad = () => {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    };
}
