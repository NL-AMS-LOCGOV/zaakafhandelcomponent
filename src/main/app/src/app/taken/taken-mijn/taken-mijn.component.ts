/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
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
import {WerklijstData} from '../../shared/dynamic-table/model/werklijstdata';

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

    werklijstData: WerklijstData;

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private sessionStorageService: SessionStorageService, private cd: ChangeDetectorRef) { }

    ngOnInit() {
        this.utilService.setTitle('title.taken.mijn');
        this.dataSource = new TakenMijnDatasource(this.takenService, this.utilService);

        this.werklijstData = this.sessionStorageService.getSessionStorage('mijnTakenWerkvoorraadData') as WerklijstData;

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

        this.sessionStorageService.setSessionStorage('mijnTakenWerkvoorraadData', werklijstData);
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
            ['naam', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaakIdentificatie', ColumnPickerValue.VISIBLE],
            ['zaaktypeOmschrijving', ColumnPickerValue.VISIBLE],
            ['creatiedatumTijd', ColumnPickerValue.VISIBLE],
            ['streefdatum', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    resetSearchCriteria() {
        this.dataSource.filters = {};
        this.dataSource.initColumns(this.initialColumns());
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = 25;
        this.sort.active = '';
        this.sort.direction = '';
    }

}
