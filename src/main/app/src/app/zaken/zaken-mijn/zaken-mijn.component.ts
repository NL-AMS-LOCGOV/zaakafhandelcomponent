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
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {DatumOverschredenPipe} from '../../shared/pipes/datumOverschreden.pipe';

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

    constructor(private zakenService: ZakenService, public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource = new ZakenMijnDatasource(this.zakenService, this.utilService);
        this.zakenService.listZaaktypes().subscribe(zaaktypes => {
            const zaaktypeColumn: TableColumn = new TableColumn('zaaktype', 'zaaktype', true);
            zaaktypeColumn.filter = new TableColumnFilter<Zaaktype>('zaaktype', zaaktypes, 'doel', 'identificatie');

            const startdatum: TableColumn = new TableColumn('startdatum', 'startdatum', true, 'startdatum')
            .pipe(DatumPipe);

            const einddatum: TableColumn = new TableColumn('einddatum', 'einddatum')
            .pipe(DatumPipe);

            const einddatumGepland: TableColumn = new TableColumn('einddatumGepland', 'einddatumGepland')
            .pipe(DatumOverschredenPipe,'einddatum');

            const uiterlijkeEinddatumAfdoening: TableColumn = new TableColumn('uiterlijkeEinddatumAfdoening','uiterlijkeEinddatumAfdoening')
            .pipe(DatumOverschredenPipe,'einddatum');

            this.dataSource.columns = [
                new TableColumn('zaak.identificatie', 'identificatie', true),
                new TableColumn('status', 'status', true),
                zaaktypeColumn,
                new TableColumn('groep', 'groep.naam', false),
                startdatum,
                einddatum,
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
