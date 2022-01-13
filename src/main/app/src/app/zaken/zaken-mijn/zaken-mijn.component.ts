/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
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
import {ZakenMijnDatasource} from './zaken-mijn-datasource';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {TableColumnFilter} from '../../shared/dynamic-table/filter/table-column-filter';
import {Zaaktype} from '../model/zaaktype';

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

    columnZaakIdentificatie: TableColumn;
    columnStatus: TableColumn;
    columnZaaktype: TableColumn;
    columnGroep: TableColumn;
    columnStartdatum: TableColumn;
    columnEinddatum: TableColumn;
    columnEinddatumGepland: TableColumn;
    columnAanvrager: TableColumn;
    columnUiterlijkeEinddatumAfdoening: TableColumn;
    columnToelichting: TableColumn;
    columnUrl: TableColumn;

    constructor(private zakenService: ZakenService, public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource = new ZakenMijnDatasource(this.zakenService, this.utilService);
        this.zakenService.listZaaktypes().subscribe(zaaktypes => {
            this.columnZaakIdentificatie = new TableColumn('zaak.identificatie', 'identificatie', true);
            this.columnStatus = new TableColumn('status', 'status', true);
            this.columnZaaktype = new TableColumn('zaaktype', 'zaaktype', true);
            this.columnZaaktype.filter = new TableColumnFilter<Zaaktype>('zaaktype', zaaktypes, 'omschrijving',
                'identificatie');
            this.columnGroep = new TableColumn('groep', 'groep', true);
            this.columnStartdatum = new TableColumn('startdatum', 'startdatum', true, 'startdatum').pipe(DatumPipe);
            this.columnEinddatum = new TableColumn('einddatum', 'einddatum').pipe(DatumPipe);
            this.columnEinddatumGepland = new TableColumn('einddatumGepland', 'einddatumGepland');
            this.columnAanvrager = new TableColumn('aanvrager', 'aanvrager', true);
            this.columnUiterlijkeEinddatumAfdoening = new TableColumn('uiterlijkeEinddatumAfdoening',
                'uiterlijkeEinddatumAfdoening');
            this.columnToelichting = new TableColumn('toelichting', 'toelichting');
            this.columnUrl = new TableColumn('url', 'url', true, null, true);

            this.dataSource.columns = [
                this.columnZaakIdentificatie,
                this.columnStatus,
                this.columnZaaktype,
                this.columnGroep,
                this.columnStartdatum,
                this.columnEinddatum,
                this.columnEinddatumGepland,
                this.columnAanvrager,
                this.columnUiterlijkeEinddatumAfdoening,
                this.columnToelichting,
                this.columnUrl
            ];
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

}
