/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {ZaakafhandelParametersService} from '../zaakafhandel-parameters.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';
import {AdminComponent} from '../admin/admin.component';

@Component({
    templateUrl: './parameters.component.html',
    styleUrls: ['./parameters.component.less']
})
export class ParametersComponent extends AdminComponent implements OnInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();
    loading: boolean = false;

    constructor(private zaakafhandelParametersService: ZaakafhandelParametersService, public utilService: UtilService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.setupMenu('title.parameters');
        this.getZaakafhandelParameters();
    }

    private getZaakafhandelParameters(): void {
        this.loading = true;
        this.zaakafhandelParametersService.listZaakafhandelParameters().subscribe(parameters => {
            this.loading = false;
            this.parameters.data = parameters;
        });
    }
}
