/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';

import {detailExpand} from '../../shared/animations/animations';

import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {UtilService} from '../../core/service/util.service';
import {ZakenService} from '../zaken.service';
import {MatTable} from '@angular/material/table';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ZakenMijnDatasource} from './zaken-mijn-datasource';

@Component({
    selector: 'zac-zaken-mijn',
    templateUrl: './zaken-mijn.component.html',
    styleUrls: ['./zaken-mijn.component.less'],
    animations: [detailExpand]
})
export class ZakenMijnComponent implements AfterViewInit, OnInit {

    dataSource: ZakenMijnDatasource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakZoekObject>;
    defaults: ZoekParameters;
    expandedRow: ZaakZoekObject | null;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService, private zoekenService: ZoekenService, public utilService: UtilService) {
        this.dataSource = new ZakenMijnDatasource(this.zoekenService, this.utilService);
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource.initColumns(this.defaultColumns());
    }

    defaultColumns(): Map<string, ColumnPickerValue> {
        return new Map([
            ['zaak.identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['omschrijving', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.HIDDEN],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['openstaandeTaken', ColumnPickerValue.VISIBLE],
            ['einddatum', ColumnPickerValue.HIDDEN],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['dagenTotStreefdatum', ColumnPickerValue.VISIBLE],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['dagenTotFataledatum', ColumnPickerValue.VISIBLE],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
        this.dataSource.load();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    resetSearch(): void {
        this.dataSource.reset();
    }

    resetColumns(): void {
        this.dataSource.resetColumns();
    }

    filtersChange(): void {
        this.paginator.pageIndex = 0;
        this.dataSource.load();
    }
}
