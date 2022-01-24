/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
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

    constructor(private zakenService: ZakenService, public utilService: UtilService,
                private identityService: IdentityService, private cdRef: ChangeDetectorRef) { }

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
        this.dataSource.columns = [
            'identificatie', 'status', 'zaaktype', 'groep', 'startdatum', 'einddatum', 'einddatumGepland',
            'aanvrager', 'behandelaar', 'uiterlijkeEinddatumAfdoening', 'toelichting', 'url'
        ];
        this.dataSource.selectedColumns = [
            'identificatie', 'status', 'zaaktype', 'startdatum', 'aanvrager', 'url'
        ];
        this.dataSource.detailExpandColumns = ['einddatumGepland', 'uiterlijkeEinddatumAfdoening', 'toelichting'];
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
        this.paginator.firstPage();
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    updateColumns() {
        this.dataSource.setFilterColumns();
        this.cdRef.detectChanges();
    }

}
