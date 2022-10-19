/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';

import {detailExpand} from '../../shared/animations/animations';

import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {UtilService} from '../../core/service/util.service';
import {IdentityService} from '../../identity/identity.service';
import {MatTable} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';

import {FilterVeld} from 'src/app/zoeken/model/filter-veld';
import {ZoekVeld} from 'src/app/zoeken/model/zoek-veld';
import {SorteerVeld} from 'src/app/zoeken/model/sorteer-veld';
import {DatumVeld} from 'src/app/zoeken/model/datum-veld';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';
import {TakenService} from '../taken.service';
import {ActivatedRoute} from '@angular/router';

import {TakenMijnDatasource} from './taken-mijn-datasource';
import {TranslateService} from '@ngx-translate/core';
import {CsvService} from '../../csv/csv.service';
import {ZoekenColumn} from '../../shared/dynamic-table/model/zoeken-column';

@Component({
    templateUrl: './taken-mijn.component.html',
    styleUrls: ['./taken-mijn.component.less'],
    animations: [detailExpand]
})
export class TakenMijnComponent implements AfterViewInit, OnInit {

    dataSource: TakenMijnDatasource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<TaakZoekObject>;
    expandedRow: TaakZoekObject | null;
    zoekenColumn = ZoekenColumn;
    zoekVeld = ZoekVeld;
    sorteerVeld = SorteerVeld;
    filterVeld = FilterVeld;
    datumVeld = DatumVeld;

    streefdatumIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private identityService: IdentityService, private zoekenService: ZoekenService,
                private translateService: TranslateService, private csvService: CsvService) {
        this.dataSource = new TakenMijnDatasource(this.zoekenService, this.utilService);
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.taken.mijn');
        this.dataSource.initColumns(this.defaultColumns());
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    defaultColumns(): Map<ZoekenColumn, ColumnPickerValue> {
        return new Map([
            [ZoekenColumn.NAAM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.ZAAK_IDENTIFICATIE, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.ZAAK_OMSCHRIJVING, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.ZAAK_TOELICHTING, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.ZAAKTYPE_OMSCHRIJVING, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.CREATIEDATUM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.STREEFDATUM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.DAGEN_TOT_STREEFDATUM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.GROEP, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.TOELICHTING, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.URL, ColumnPickerValue.STICKY]
        ]);
    }

    resetColumns(): void {
        this.dataSource.resetColumns();
    }

    filtersChange(): void {
        this.dataSource.filtersChanged();
    }

    downloadCSV(): void {
        this.csvService.exportToCSV(this.dataSource.zoekParameters).subscribe(response => {
            this.utilService.downloadBlobResponse(response, this.translateService.instant('title.taken.mijn'));
        });
    }
}
