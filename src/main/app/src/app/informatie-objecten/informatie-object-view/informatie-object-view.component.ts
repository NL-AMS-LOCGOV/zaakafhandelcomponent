/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieObject} from '../model/enkelvoudig-informatie-object';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {ActivatedRoute} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';
import {ZaakInformatieObjectKoppeling} from '../model/zaak-informatie-object-koppeling';
import {DownloadMenuItem} from '../../shared/side-nav/menu-item/download-menu-item';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';

@Component({
    templateUrl: './informatie-object-view.component.html',
    styleUrls: ['./informatie-object-view.component.less']
})
export class InformatieObjectViewComponent extends AbstractView implements OnInit {

    infoObject: EnkelvoudigInformatieObject;
    menu: MenuItem[];
    zaken: ZaakInformatieObjectKoppeling[];
    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    constructor(store: Store<State>,
                private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                private titleService: Title,
                public utilService: UtilService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.infoObject = data['informatieObject'];
            this.titleService.setTitle(`${this.infoObject.identificatie} | Document`);
            this.utilService.setHeaderTitle(`${this.infoObject.identificatie} | Document`);

            this.setupMenu();
            this.loadZaken();
        });
    }

    private setupMenu(): void {
        this.menu = [
            new HeaderMenuItem('Document'),
            new DownloadMenuItem('Downloaden', `/zac/rest/informatieobjecten/informatieobject/${this.infoObject.uuid}/download`, this.infoObject.bestandsnaam,
                'save_alt')
        ];
    }

    private loadZaken(): void {
        this.informatieObjectenService.getZaakKoppelingen(this.infoObject.uuid).subscribe(zaken => {
            this.zaken = zaken;
        });
    }

}
