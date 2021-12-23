/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {UtilService} from '../../core/service/util.service';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {AdminService} from '../admin.service';
import {MatTableDataSource} from '@angular/material/table';
import {AuditTrailRegel} from '../../shared/audit/model/audit-trail-regel';
import {Zaaktype} from '../../zaken/model/zaaktype';
import {ZaakafhandelParameters} from '../model/zaakafhandel-parameters';

@Component({
    templateUrl: './params.component.html',
    styleUrls: ['./params.component.less']
})
export class ParamsComponent extends AbstractView implements OnInit {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;
    menu: MenuItem[] = [];
    parameters: MatTableDataSource<ZaakafhandelParameters> = new MatTableDataSource<ZaakafhandelParameters>();

    constructor(store: Store<State>, private adminService: AdminService, public utilService: UtilService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.menu = [];
        this.menu.push(new HeaderMenuItem('params'));
        this.utilService.setTitle('title.params');
        this.getZaakafhandelParameters();
    }

    private getZaakafhandelParameters(): void {
        this.adminService.listZaakafhandelParameters().subscribe(parameters => {
            this.parameters.data = parameters;
        });
    }
}
