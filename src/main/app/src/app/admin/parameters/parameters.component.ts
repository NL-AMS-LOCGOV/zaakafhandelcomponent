/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, OnInit, ViewChild} from '@angular/core';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {ViewComponent} from '../../shared/abstract-view/view-component';
import {MatSort} from '@angular/material/sort';
import {merge} from 'rxjs';
import {map, startWith, switchMap} from 'rxjs/operators';

@Component({
    templateUrl: './parameters.component.html',
    styleUrls: ['./parameters.component.less']
})
export class ParametersComponent extends ViewComponent implements OnInit, AfterViewInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('parametersSort') parametersSort: MatSort;

    filterChange: EventEmitter<void> = new EventEmitter<void>();
    menu: MenuItem[] = [];
    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();
    loading: boolean = false;

    constructor(private zaakafhandelParametersService: ZaakafhandelParametersService, public utilService: UtilService) {
        super();
    }

    ngOnInit(): void {
        this.menu = [];
        this.menu.push(new HeaderMenuItem('actie.admin'));
        this.utilService.setTitle('title.parameters');
        this.getZaakafhandelParameters();
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        this.parameters.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'omschrijving':
                    return item.zaaktype.omschrijving;
                case 'model':
                    return item.caseDefinition?.naam;
                case'geldig':
                    return item.zaaktype.nuGeldig;
                case'beginGeldigheid':
                    return item.zaaktype.beginGeldigheid;
                case'eindeGeldigheid':
                    return item.zaaktype.eindeGeldigheid;
                default:
                    return item[property];
            }
        };

        this.parameters.sort = this.parametersSort;

    }

    private getZaakafhandelParameters(): void {
        this.zaakafhandelParametersService.listZaakafhandelParameters().subscribe(parameters => {
            this.loading = false;
            this.parameters.data = parameters;
        });
    }
}
