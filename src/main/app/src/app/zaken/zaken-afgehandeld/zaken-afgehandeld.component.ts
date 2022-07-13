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
import {ZoekenService} from '../../zoeken/zoeken.service';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ZoekVeld} from '../../zoeken/model/zoek-veld';
import {SorteerVeld} from '../../zoeken/model/sorteer-veld';
import {FilterVeld} from '../../zoeken/model/filter-veld';
import {DatumVeld} from '../../zoeken/model/datum-veld';
import {ZakenAfgehandeldDatasource} from './zaken-afgehandeld-datasource';

@Component({
    templateUrl: './zaken-afgehandeld.component.html',
    styleUrls: ['./zaken-afgehandeld.component.less'],
    animations: [detailExpand]
})
export class ZakenAfgehandeldComponent implements AfterViewInit, OnInit {

    dataSource: ZakenAfgehandeldDatasource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakZoekObject>;
    expandedRow: ZaakZoekObject | null;
    ZoekVeld = ZoekVeld;
    SorteerVeld = SorteerVeld;
    FilterVeld = FilterVeld;
    DatumVeld = DatumVeld;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService, private zoekenService: ZoekenService, public utilService: UtilService) {
        this.dataSource = new ZakenAfgehandeldDatasource(this.zoekenService, this.utilService);
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.afgehandeld');
        this.dataSource.initColumns(this.defaultColumns());
    }

    defaultColumns(): Map<string, ColumnPickerValue> {
        return new Map([
            ['zaak.identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.HIDDEN],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['omschrijving', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.HIDDEN],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['einddatum', ColumnPickerValue.VISIBLE],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['behandelaar', ColumnPickerValue.VISIBLE],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['resultaat', ColumnPickerValue.VISIBLE],
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

    resetColumns(): void {
        this.dataSource.resetColumns();
    }

    filtersChange(): void {
        this.dataSource.filtersChanged();
    }
}
