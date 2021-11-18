/*
 * SPDX-FileCopyrightText: 2021 Atos
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
        const startdatum: TableColumn = new TableColumn('startdatum', 'startdatum', true, 'startdatum').pipe(DatumPipe);

        const einddatum: TableColumn = new TableColumn('einddatum', 'einddatum', true).pipe(DatumPipe);

        const einddatumGepland: TableColumn = new TableColumn('einddatumGepland', 'einddatumGepland').pipe(DatumPipe);

        const uiterlijkeEinddatumAfdoening: TableColumn = new TableColumn('uiterlijkeEinddatumAfdoening', 'uiterlijkeEinddatumAfdoening').pipe(DatumPipe);

        this.dataSource.columns = [
            new TableColumn('zaak.identificatie', 'identificatie', true),
            this.dataSource.zoekParameters.selectie === 'groep' ?
                new TableColumn('zaaktype', 'zaaktype', true) :
                new TableColumn('groep', 'groep.naam', true),
            startdatum,
            einddatum,
            einddatumGepland,
            new TableColumn('aanvrager', 'aanvrager', true),
            new TableColumn('behandelaar', 'behandelaar.naam', true),
            uiterlijkeEinddatumAfdoening,
            new TableColumn('resultaat', 'resultaat.naam', true),
            new TableColumn('toelichting', 'toelichting'),
            new TableColumn('url', 'url', true, null, true)
        ];
    }

    private zaaktypesOphalen() {
        this.zakenService.getZaaktypes().subscribe(zaakTypes => {
            this.zaakTypes = zaakTypes;
        });
    }

    private groepenOphalen() {
        this.identityService.getGroepen().subscribe(groepen => {
            this.groepen = groepen;
        });
    }

    zoekZaken() {
        this.dataSource.zoekZaken();
        this.setColumns();
    }

}
