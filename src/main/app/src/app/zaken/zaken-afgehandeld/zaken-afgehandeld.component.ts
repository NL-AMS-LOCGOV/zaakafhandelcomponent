/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
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
import {WerklijstData} from '../../shared/dynamic-table/model/werklijstdata';

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

    werklijstData: WerklijstData;

    constructor(private zakenService: ZakenService, public utilService: UtilService,
                private identityService: IdentityService, private sessionStorageService: SessionStorageService, private cd: ChangeDetectorRef) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.afgehandeld');
        this.dataSource = new ZakenAfgehandeldDatasource(this.zakenService, this.utilService);

        this.zaaktypesOphalen();
        this.groepenOphalen();

        this.werklijstData = this.sessionStorageService.getSessionStorage(
            'afgehandeldeZakenWerkvoorraadData') as WerklijstData;

        if (this.werklijstData) {
            this.dataSource.zoekParameters = this.werklijstData.searchParameters;
        }

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

            if (this.dataSource.zoekParameters.groep !== null || this.dataSource.zoekParameters.zaaktype !== null) {
                this.searchCases();
            }
        }
    }

    ngOnDestroy() {
        const flatListColumns = JSON.stringify([...this.dataSource.columns]);
        const werklijstData = new WerklijstData();
        werklijstData.searchParameters = this.dataSource.zoekParameters;
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
        this.setColumns();
        if (this.dataSource.zoekParameters[this.dataSource.zoekParameters.selectie]) {
            this.searchAndGoToFirstPage();
        }
    }

    searchAndGoToFirstPage() {
        this.searchCases();
        this.paginator.firstPage();
    }

    searchCases() {
        this.dataSource.zoekZaken();
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

    private setColumns() {
        if (this.werklijstData) {
            const mapColumns: Map<string, ColumnPickerValue> = new Map(JSON.parse(this.werklijstData.columns));
            this.toggleGroepOrZaaktype(mapColumns);
        } else {
            this.toggleGroepOrZaaktype(this.initialColumns());
        }
    }

    initialColumns(): Map<string, ColumnPickerValue> {
        return new Map([
            ['identificatie', ColumnPickerValue.VISIBLE],
            ['status', ColumnPickerValue.HIDDEN],
            ['zaaktype', ColumnPickerValue.VISIBLE],
            ['groep', ColumnPickerValue.HIDDEN],
            ['startdatum', ColumnPickerValue.VISIBLE],
            ['einddatum', ColumnPickerValue.VISIBLE],
            ['einddatumGepland', ColumnPickerValue.HIDDEN],
            ['aanvrager', ColumnPickerValue.VISIBLE],
            ['behandelaar', ColumnPickerValue.VISIBLE],
            ['uiterlijkeEinddatumAfdoening', ColumnPickerValue.HIDDEN],
            ['toelichting', ColumnPickerValue.HIDDEN],
            ['resultaat', ColumnPickerValue.VISIBLE],
            ['url', ColumnPickerValue.STICKY]
        ]);
    }

    toggleGroepOrZaaktype(columns: Map<string, ColumnPickerValue>): Map<string, ColumnPickerValue> {
        if (this.dataSource.zoekParameters.selectie === 'groep') {
            columns.set('groep', ColumnPickerValue.HIDDEN);
            columns.set('zaaktype', ColumnPickerValue.VISIBLE);
        } else {
            columns.set('groep', ColumnPickerValue.VISIBLE);
            columns.set('zaaktype', ColumnPickerValue.HIDDEN);
        }

        this.dataSource.initColumns(columns);

        return columns;
    }

    resetSearchCriteria() {
        this.dataSource.zoekParameters = {
            selectie: 'groep',
            groep: null,
            zaaktype: null
        };
        this.dataSource.filters = {};
        this.dataSource.initColumns(this.initialColumns());
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = 25;
        this.sort.active = '';
        this.sort.direction = '';
    }
}
