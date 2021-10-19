/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Taak} from '../model/taak';
import {MatButtonToggle} from '@angular/material/button-toggle';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {TakenMijnDatasource} from './taken-mijn-datasource';
import {TaakSortering} from '../model/taak-sortering';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';

@Component({
    templateUrl: './taken-mijn.component.html',
    styleUrls: ['./taken-mijn.component.less'],
    animations: [detailExpand]
})
export class TakenMijnComponent implements AfterViewInit, OnInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<Taak>;
    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;

    dataSource: TakenMijnDatasource;
    expandedRow: Taak | null;

    constructor(private route: ActivatedRoute, private takenService: TakenService, private titleService: Title, public utilService: UtilService) {
    }

    ngOnInit() {
        this.titleService.setTitle('Mijn taken');
        this.utilService.setHeaderTitle('Mijn taken');
        this.dataSource = new TakenMijnDatasource(this.takenService, this.utilService);

        const creatieDatum: TableColumn = new TableColumn('creatiedatum', 'creatiedatumTijd', true, TaakSortering.CREATIEDATUM);
        creatieDatum.pipe = DatumPipe;

        const streefDatum: TableColumn = new TableColumn('streefdatum', 'streefdatum', true, TaakSortering.STREEFDATUM);
        streefDatum.pipe = DatumPipe;

        this.dataSource.columns = [
            new TableColumn('naam', 'naam', true, TaakSortering.TAAKNAAM),
            new TableColumn('status', 'status', true),
            new TableColumn('hoort.bij.zaaknummer', 'zaakIdentificatie', true),
            new TableColumn('zaaktype', 'zaaktypeOmschrijving', true),
            creatieDatum,
            streefDatum,
            new TableColumn('groep', 'groep.naam', true),
            new TableColumn('url', 'url', true, null, true)
        ];
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }
}
