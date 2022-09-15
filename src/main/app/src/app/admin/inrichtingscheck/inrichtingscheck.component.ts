/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {AdminComponent} from '../admin/admin.component';
import {ToggleSwitchOptions} from '../../zoeken/toggle-filter/toggle-switch-options';
import {HealthCheckService} from '../health-check.service';
import {ZaaktypeInrichtingscheck} from '../model/zaaktype-inrichtingscheck';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
    templateUrl: './inrichtingscheck.component.html',
    styleUrls: ['./inrichtingscheck.component.less'],
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
        ])
    ]
})
export class InrichtingscheckComponent extends AdminComponent implements OnInit, AfterViewInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild(MatSort) sort: MatSort;

    dataSource: MatTableDataSource<ZaaktypeInrichtingscheck> = new MatTableDataSource<ZaaktypeInrichtingscheck>();
    loading: boolean = true;
    columnsToDisplay = ['valide', 'expand', 'zaaktypeOmschrijving', 'zaaktypeDoel', 'beginGeldigheid'];
    zaaktypes: ZaaktypeInrichtingscheck[];
    expandedRow: ZaaktypeInrichtingscheck | null;
    valideFilter: ToggleSwitchOptions = ToggleSwitchOptions.UNCHECKED;
    filterValue: string = '';

    constructor(private healtCheckService: HealthCheckService, public utilService: UtilService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.setupMenu('title.inrichtingscheck');
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        this.dataSource.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'zaaktypeOmschrijving':
                    return item.zaaktype.omschrijving.toLowerCase();
                case 'zaaktypeDoel':
                    return item.zaaktype.doel;
                case'beginGeldigheid':
                    return item.zaaktype.beginGeldigheid;
                default:
                    return item[property];
            }
        };
        this.dataSource.sort = this.sort;
        this.dataSource.filterPredicate = (data, filter: string) => {
            if (this.valideFilter === ToggleSwitchOptions.CHECKED && !data.valide) {
                return false;
            }
            if (this.valideFilter === ToggleSwitchOptions.UNCHECKED && data.valide) {
                return false;
            }
            const dataString = (data.zaaktype.omschrijving + ' ' + data.zaaktype.doel).trim().toLowerCase();
            return dataString.indexOf(filter.trim().toLowerCase()) !== -1;
        };

        this.healtCheckService.listZaaktypeInrichtingschecks().subscribe(value => {
            this.loading = false;
            this.dataSource.data = value.sort((a, b) =>
                a.zaaktype.omschrijving.localeCompare(b.zaaktype.omschrijving)
            );
            this.applyFilter();
        });
    }

    applyFilter(event?: Event) {
        if (event) {
            const filterValue = (event.target as HTMLInputElement).value;
            this.filterValue = filterValue.trim().toLowerCase();
            this.dataSource.filter = filterValue;
        } else { // toggleSwitch
            this.dataSource.filter = ' ' + this.filterValue;
        }
    }
}
