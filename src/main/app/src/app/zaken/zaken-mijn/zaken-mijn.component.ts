/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {ZakenService} from '../zaken.service';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {MatTable} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {TableColumnFilter} from '../../shared/dynamic-table/filter/table-column-filter';
import {Zaaktype} from '../model/zaaktype';
import {ZakenMijnDatasource} from './zaken-mijn-datasource';
import {Title} from '@angular/platform-browser';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';

@Component({
    templateUrl: './zaken-mijn.component.html',
    styleUrls: ['./zaken-mijn.component.less'],
    animations: [detailExpand]
})
export class ZakenMijnComponent implements OnInit, AfterViewInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakOverzicht>;
    dataSource: ZakenMijnDatasource;
    expandedRow: ZaakOverzicht | null;

    constructor(private zakenService: ZakenService, private titleService: Title, public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.titleService.setTitle('Mijn zaken');
        this.utilService.setHeaderTitle('Mijn zaken');
        this.dataSource = new ZakenMijnDatasource(this.zakenService, this.utilService);
        this.zakenService.getZaaktypes().subscribe(zaaktypes => {
            const zaaktypeColumn: TableColumn = new TableColumn('zaaktype', 'zaaktype', true);
            zaaktypeColumn.filter = new TableColumnFilter<Zaaktype>('zaaktype', zaaktypes, 'doel', 'identificatie');

            const startdatum: TableColumn = new TableColumn('startdatum', 'startdatum', true, 'startdatum');
            startdatum.pipe = DatumPipe;

            const einddatumGepland: TableColumn = new TableColumn('einddatumGepland', 'einddatumGepland');
            einddatumGepland.pipe = DatumPipe;

            const uiterlijkeEinddatumAfdoening: TableColumn = new TableColumn('uiterlijkeEinddatumAfdoening',
                'uiterlijkeDatumAfdoening');
            uiterlijkeEinddatumAfdoening.pipe = DatumPipe;

            this.dataSource.columns = [
                new TableColumn('zaaknummer', 'identificatie', true),
                new TableColumn('status', 'status', true),
                zaaktypeColumn,
                new TableColumn('groep', 'groep.naam', false),
                startdatum,
                einddatumGepland,
                new TableColumn('aanvrager', 'aanvrager', true),
                uiterlijkeEinddatumAfdoening,
                new TableColumn('toelichting', 'toelichting'),
                new TableColumn('url', 'url', true, null, true)
            ];
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }

}
