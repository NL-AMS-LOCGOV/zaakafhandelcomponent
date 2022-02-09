/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Taak} from '../model/taak';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {UtilService} from '../../core/service/util.service';
import {TakenMijnDatasource} from './taken-mijn-datasource';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {TextIcon} from '../../shared/edit/text-icon';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {SessionStorageService} from '../../shared/storage/session-storage.service';

@Component({
    templateUrl: './taken-mijn.component.html',
    styleUrls: ['./taken-mijn.component.less'],
    animations: [detailExpand]
})
export class TakenMijnComponent implements AfterViewInit, OnInit, OnDestroy {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<Taak>;

    dataSource: TakenMijnDatasource;
    expandedRow: Taak | null;

    streefdatumIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem', 'warningTaakVerlopen_icon',
        'msg.datum.overschreden', 'warning');

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private sessionStorageService: SessionStorageService) { }

    ngOnInit() {
        this.utilService.setTitle('title.taken.mijn');
        this.dataSource = new TakenMijnDatasource(this.takenService, this.utilService);

        this.dataSource.initColumns(new Map([
            ['naam', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaakIdentificatie', ColumnPickerValue.VISIBLE],
            ['zaaktypeOmschrijving', ColumnPickerValue.VISIBLE],
            ['creatiedatumTijd', ColumnPickerValue.VISIBLE],
            ['streefdatum', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['url', ColumnPickerValue.STICKY]
        ]));
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }

    ngOnDestroy() {
        const werklijstData = {
            filters: this.dataSource.filters,
            columns: {
                allColumns: this.dataSource.columns,
                visibleColumns: this.dataSource.visibleColumns,
                detailExpandColumns: this.dataSource.detailExpandColumns
            },
            sorting: {
                column: this.sort.active,
                direction: this.sort.direction
            },
            paginator: {
                page: this.paginator.pageIndex,
                pageSize: this.paginator.pageSize
            }
        };

        this.sessionStorageService.setSessionStorage('mijnTakenWerkvoorraadData', werklijstData);
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

}
