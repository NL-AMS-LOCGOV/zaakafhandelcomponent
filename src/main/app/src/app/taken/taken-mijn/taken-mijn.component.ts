/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
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
import {UtilService} from '../../core/service/util.service';
import {TakenMijnDatasource} from './taken-mijn-datasource';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {TextIcon} from '../../shared/edit/text-icon';
import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';

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

    streefdatumIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem', 'warningTaakVerlopen_icon',
        'msg.datum.overschreden', 'warning');

    constructor(private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService) {
    }

    ngOnInit() {
        this.utilService.setTitle('title.taken.mijn');
        this.dataSource = new TakenMijnDatasource(this.takenService, this.utilService);

        this.dataSource.initColumns({
            naam: ColumnPickerValue.VISIBLE,
            status: ColumnPickerValue.VISIBLE,
            zaakIdentificatie: ColumnPickerValue.VISIBLE,
            zaaktypeOmschrijving: ColumnPickerValue.VISIBLE,
            creatiedatumTijd: ColumnPickerValue.VISIBLE,
            streefdatum: ColumnPickerValue.VISIBLE,
            groep: ColumnPickerValue.VISIBLE,
            url: ColumnPickerValue.STICKY
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
