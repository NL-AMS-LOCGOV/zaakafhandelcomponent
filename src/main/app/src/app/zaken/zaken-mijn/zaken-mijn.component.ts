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
import {SessionStorageService} from '../../shared/storage/session-storage.service';

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

    zaaktypeValue: any;

    constructor(private zakenService: ZakenService, public utilService: UtilService, private cdRef: ChangeDetectorRef,
                private sessionStorageService: SessionStorageService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource = new ZakenMijnDatasource(this.zakenService, this.utilService);
        this.zakenService.listZaaktypes().subscribe(zaaktypes => {
            this.zaaktypes = zaaktypes;

            this.dataSource.columns = [
                'identificatie', 'status', 'zaaktype', 'groep', 'startdatum', 'einddatum', 'einddatumGepland', 'aanvrager', 'uiterlijkeEinddatumAfdoening', 'toelichting', 'url'
            ];
            this.dataSource.visibleColumns = [
                'identificatie', 'status', 'zaaktype', 'startdatum', 'aanvrager', 'url'
            ];
            this.dataSource.selectedColumns = this.dataSource.visibleColumns;
            this.dataSource.detailExpandColumns = ['groep', 'einddatum', 'einddatumGepland', 'uiterlijkeEinddatumAfdoening', 'toelichting'];
            this.dataSource.setFilterColumns();
        });
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
                selectedColumns: this.dataSource.selectedColumns,
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

        this.sessionStorageService.setSessionStorage('mijnZakenWerkvoorraadData', werklijstData);
    }

    updateColumns() {
        this.dataSource.setFilterColumns();
        this.cdRef.detectChanges();
    }

    zaaktypeChange(zaaktype: Zaaktype) {
        this.dataSource.filter('zaaktype', zaaktype?.identificatie);
        this.paginator.firstPage();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

}
