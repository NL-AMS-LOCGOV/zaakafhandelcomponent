/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {DashboardCard} from '../model/dashboard-card';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';

@Component({
    template: '',
    styleUrls: ['./dashboard-card.component.less']
})
export abstract class DashboardCardComponent<T> implements OnInit, AfterViewInit {

    @Input() data: DashboardCard;

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    dataSource: MatTableDataSource<T> = new MatTableDataSource<T>();

    abstract columns: string[];

    constructor() { }

    ngOnInit(): void {
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
}
