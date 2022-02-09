/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {Zaaktype} from '../model/zaaktype';
import {ZakenAfgehandeldDatasource} from './zaken-afgehandeld-datasource';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {IdentityService} from '../../identity/identity.service';
import {Groep} from '../../identity/model/groep';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {TextIcon} from '../../shared/edit/text-icon';
import {SessionStorageService} from '../../shared/storage/session-storage.service';

@Component({
    templateUrl: './zaken-afgehandeld.component.html',
    styleUrls: ['./zaken-afgehandeld.component.less'],
    animations: [detailExpand]
})
export class ZakenAfgehandeldComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakOverzicht>;
    dataSource: ZakenAfgehandeldDatasource;
    expandedRow: ZaakOverzicht | null;
    groepen: Groep[] = [];
    zaakTypes: Zaaktype[] = [];

    constructor(private zakenService: ZakenService, public utilService: UtilService,
                private identityService: IdentityService, private sessionStorageService: SessionStorageService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.afgehandeld');
        this.dataSource = new ZakenAfgehandeldDatasource(this.zakenService, this.utilService);
        this.dataSource.initColumns(new Map([
            ['identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.VISIBLE],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.VISIBLE],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['einddatum', ColumnPickerValue.HIDDEN],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['aanvrager', ColumnPickerValue.VISIBLE],
            ['behandelaar', ColumnPickerValue.HIDDEN],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['url', ColumnPickerValue.STICKY]
        ]));

        this.zaaktypesOphalen();
        this.groepenOphalen();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    ngOnDestroy() {
        const werklijstData = {
            searchParameters: this.dataSource.zoekParameters,
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

        this.sessionStorageService.setSessionStorage('afgehandeldeZakenWerkvoorraadData', werklijstData);
    }

    private zaaktypesOphalen() {
        this.zakenService.listZaaktypes().subscribe(zaakTypes => {
            this.zaakTypes = zaakTypes;
        });
    }

    private groepenOphalen() {
        this.identityService.listGroepen().subscribe(groepen => {
            this.groepen = groepen;
        });
    }

    switchTypeAndSearch() {
        if (this.dataSource.zoekParameters[this.dataSource.zoekParameters.selectie]) {
            this.zoekZaken();
        }
    }

    zoekZaken() {
        this.dataSource.zoekZaken();
        this.paginator.firstPage();
    }

    isAfterDate(datum, actual): boolean {
        return Conditionals.isOverschreden(datum, actual);
    }

    getIcon(row, icon: string): TextIcon {
        switch (icon) {
            case 'einddatumGepland':
                return new TextIcon(Conditionals.isAfterDate(row.einddatum), 'report_problem',
                    'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
            case 'uiterlijkeEinddatumAfdoening':
                return new TextIcon(Conditionals.isAfterDate(row.einddatum), 'report_problem',
                    'errorVerlopen_icon', 'msg.datum.overschreden', 'error');
            default:
                throw new Error(`Unknown icon field: ${icon}`);
        }
    }
}
