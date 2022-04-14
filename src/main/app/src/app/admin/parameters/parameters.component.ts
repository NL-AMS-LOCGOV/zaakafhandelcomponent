/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {LinkMenuItem} from '../../shared/side-nav/menu-item/link-menu-item';
import {ViewComponent} from '../../shared/abstract-view/view-component';

@Component({
    templateUrl: './parameters.component.html',
    styleUrls: ['./parameters.component.less']
})
export class ParametersComponent extends ViewComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    menu: MenuItem[] = [];
    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();
    loading: boolean;

    constructor(private adminService: ZaakafhandelParametersService, public utilService: UtilService) {
        super();
    }

    ngOnInit(): void {
        this.menu = [];
        this.menu.push(new HeaderMenuItem('actie.admin'));
        this.menu.push(new LinkMenuItem('parameters', 'parameters', 'tune'));
        this.utilService.setTitle('title.parameters');
        this.getZaakafhandelParameters();
    }

    private getZaakafhandelParameters(): void {
        this.adminService.listZaakafhandelParameters().subscribe(parameters => {
            this.loading = false;
            this.parameters.data = parameters;
        });
    }
}
