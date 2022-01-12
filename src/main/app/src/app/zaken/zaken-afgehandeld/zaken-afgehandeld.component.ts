/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {TableColumn} from '../../shared/dynamic-table/column/table-column';
import {Zaaktype} from '../model/zaaktype';
import {DatumPipe} from '../../shared/pipes/datum.pipe';
import {ZakenAfgehandeldDatasource} from './zaken-afgehandeld-datasource';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {IdentityService} from '../../identity/identity.service';
import {Groep} from '../../identity/model/groep';
import {MatButtonToggle} from '@angular/material/button-toggle';
import {detailExpand} from '../../shared/animations/animations';
import {Conditionals} from '../../shared/edit/conditional-fn';

@Component({
    templateUrl: './zaken-afgehandeld.component.html',
    styleUrls: ['./zaken-afgehandeld.component.less'],
    animations: [detailExpand]
})
export class ZakenAfgehandeldComponent implements OnInit, AfterViewInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakOverzicht>;
    @ViewChild('toggleColumns') toggleColumns: MatButtonToggle;
    dataSource: ZakenAfgehandeldDatasource;
    expandedRow: ZaakOverzicht | null;
    groepen: Groep[] = [];
    zaakTypes: Zaaktype[] = [];

    columnZaakIdentificatie: TableColumn;
    columnStatus: TableColumn;
    columnZaaktype: TableColumn;
    columnGroep: TableColumn;
    columnStartdatum: TableColumn;
    columnEinddatum: TableColumn;
    columnEinddatumGepland: TableColumn;
    columnAanvrager: TableColumn;
    columnBehandelaar: TableColumn;
    columnUiterlijkeEinddatumAfdoening: TableColumn;
    columnToelichting: TableColumn;
    columnUrl: TableColumn;

    constructor(private zakenService: ZakenService, public utilService: UtilService, private identityService: IdentityService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.afgehandeld');
        this.dataSource = new ZakenAfgehandeldDatasource(this.zakenService, this.utilService);

        this.setColumns();
        this.zaaktypesOphalen();
        this.groepenOphalen();
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    private setColumns() {
        this.columnZaakIdentificatie = new TableColumn('zaak.identificatie', 'identificatie', true);
        this.columnStatus = new TableColumn('status', 'status', true);
        this.columnZaaktype = new TableColumn('zaaktype', 'zaaktype', true);
        this.columnGroep = new TableColumn('groep', 'groep', true);
        this.columnStartdatum = new TableColumn('startdatum', 'startdatum', true, 'startdatum').pipe(DatumPipe);
        this.columnEinddatum = new TableColumn('einddatum', 'einddatum').pipe(DatumPipe);
        this.columnEinddatumGepland = new TableColumn('einddatumGepland', 'einddatumGepland');
        this.columnAanvrager = new TableColumn('aanvrager', 'aanvrager', true);
        this.columnBehandelaar = new TableColumn('behandelaar', 'behandelaar', true);
        this.columnUiterlijkeEinddatumAfdoening = new TableColumn('uiterlijkeEinddatumAfdoening',
            'uiterlijkeEinddatumAfdoening');
        this.columnToelichting = new TableColumn('toelichting', 'toelichting');
        this.columnUrl = new TableColumn('url', 'url', true, null, true);

        this.dataSource.columns = [
            this.columnZaakIdentificatie,
            this.columnStatus,
            this.dataSource.zoekParameters.selectie === 'groep' ? this.columnGroep : this.columnZaaktype,
            this.columnStartdatum,
            this.columnEinddatum,
            this.columnEinddatumGepland,
            this.columnAanvrager,
            this.columnBehandelaar,
            this.columnUiterlijkeEinddatumAfdoening,
            this.columnToelichting,
            this.columnUrl
        ];
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

    zoekZaken() {
        this.dataSource.zoekZaken();
        this.setColumns();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

}
