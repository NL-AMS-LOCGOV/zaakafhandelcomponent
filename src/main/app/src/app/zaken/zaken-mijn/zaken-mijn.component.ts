/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {MatTable} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {Zaaktype} from '../model/zaaktype';
import {ZakenMijnDatasource} from './zaken-mijn-datasource';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {TextIcon} from '../../shared/edit/text-icon';
import {WerklijstData} from '../../shared/dynamic-table/model/werklijstdata';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';

@Component({
    templateUrl: './zaken-mijn.component.html',
    styleUrls: ['./zaken-mijn.component.less'],
    animations: [detailExpand]
})
export class ZakenMijnComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakOverzicht>;
    dataSource: ZakenMijnDatasource;
    expandedRow: ZaakOverzicht | null;

    zaaktypes: Zaaktype[] = [];

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    werklijstData: WerklijstData;

    constructor(private zakenService: ZakenService, public utilService: UtilService,
                private cd: ChangeDetectorRef) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource = new ZakenMijnDatasource(this.zakenService, this.utilService);
        this.zakenService.listZaaktypes().subscribe(zaaktypes => {
            this.zaaktypes = zaaktypes;
        });

        this.werklijstData = SessionStorageUtil.getItem('mijnZakenWerkvoorraadData') as WerklijstData;

        this.setColumns();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;

        if (this.werklijstData) {
            this.dataSource.filters = this.werklijstData.filters;
            this.dataSource.zoekParameters.zaaktype = this.werklijstData.filters['zaaktype'];

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
        this.saveSearchQuery();
    }

    zaaktypeChange() {
        this.dataSource.searchCases();
        this.paginator.firstPage();
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
            ['identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.HIDDEN],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['einddatum', ColumnPickerValue.HIDDEN],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    saveSearchQuery() {
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

        SessionStorageUtil.setItem('mijnZakenWerkvoorraadData', werklijstData);
    }

    resetSearchCriteria() {
        delete this.dataSource.zoekParameters.zaaktype;
        this.dataSource.filters = {};
        this.dataSource.initColumns(this.initialColumns());
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = 25;
        this.sort.active = '';
        this.sort.direction = '';

        this.saveSearchQuery();

        this.zaaktypeChange();
    }

}
