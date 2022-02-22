/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {UtilService} from '../../core/service/util.service';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';

@Component({
    templateUrl: './parameters.component.html',
    styleUrls: ['./parameters.component.less']
})
export class ParametersComponent extends AbstractView implements OnInit {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;
    menu: MenuItem[] = [];
    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();
    loading: boolean;

    constructor(store: Store<State>, private adminService: ZaakafhandelParametersService, public utilService: UtilService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.menu = [];
        this.menu.push(new HeaderMenuItem('actie.admin'));
        this.menu.push(new LinkMenuTitem('parameters', 'parameters', 'tune'));
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
