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
import {Zaaktype} from '../model/zaaktype';
import {ZakenMijnDatasource} from './zaken-mijn-datasource';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {TextIcon} from '../../shared/edit/text-icon';

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

    zaaktypes: Zaaktype[] = [];

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService, public utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource = new ZakenMijnDatasource(this.zakenService, this.utilService);
        this.zakenService.listZaaktypes().subscribe(zaaktypes => {
            this.zaaktypes = zaaktypes;

            this.dataSource.initColumns({
                identificatie: ColumnPickerValue.VISIBLE,
                status: ColumnPickerValue.VISIBLE,
                zaaktype: ColumnPickerValue.VISIBLE,
                groep: ColumnPickerValue.VISIBLE,
                startdatum: ColumnPickerValue.VISIBLE,
                einddatum: ColumnPickerValue.HIDDEN,
                einddatumGepland: ColumnPickerValue.HIDDEN,
                aanvrager: ColumnPickerValue.VISIBLE,
                uiterlijkeEinddatumAfdoening: ColumnPickerValue.HIDDEN,
                toelichting: ColumnPickerValue.HIDDEN,
                url: ColumnPickerValue.STICKY
            });
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.dataSource.load();
        this.table.dataSource = this.dataSource;
    }

    zaaktypeChange(zaaktype: Zaaktype) {
        this.dataSource.filter('zaaktype', zaaktype?.identificatie);
        this.paginator.firstPage();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

}
