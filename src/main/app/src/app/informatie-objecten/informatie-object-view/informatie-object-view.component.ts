/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieObject} from '../model/enkelvoudig-informatie-object';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {ActivatedRoute} from '@angular/router';
import {UtilService} from '../../core/service/util.service';
import {ZaakInformatieObjectKoppeling} from '../model/zaak-informatie-object-koppeling';
import {DownloadMenuItem} from '../../shared/side-nav/menu-item/download-menu-item';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';

@Component({
    templateUrl: './informatie-object-view.component.html',
    styleUrls: ['./informatie-object-view.component.less']
})
export class InformatieObjectViewComponent extends AbstractView implements OnInit, OnDestroy {

    infoObject: EnkelvoudigInformatieObject;
    menu: MenuItem[];
    zaken: ZaakInformatieObjectKoppeling[];
    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    constructor(store: Store<State>,
                private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                public utilService: UtilService,
                private websocketService: WebsocketService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.infoObject = data['informatieObject'];
            this.utilService.setTitle('title.document', {document: this.infoObject.identificatie});

            this.websocketService.addListener(Opcode.CREATED, ObjectType.ENKELVOUDIG_INFORMATIEOBJECT, this.infoObject.uuid,
                () => this.loadInformatieObject());

            this.setupMenu();
            this.loadZaken();
        }));
    }

    ngOnDestroy() {
        this.websocketService.removeListeners(Opcode.CREATED, ObjectType.ENKELVOUDIG_INFORMATIEOBJECT, this.infoObject.uuid);
    }

    private setupMenu(): void {
        this.menu = [
            new HeaderMenuItem('informatieobject'),
            new DownloadMenuItem('actie.downloaden', `/zac/rest/informatieobjecten/informatieobject/${this.infoObject.uuid}/download`,
                this.infoObject.bestandsnaam,
                'save_alt')
        ];
    }

    private loadZaken(): void {
        this.informatieObjectenService.getZaakKoppelingen(this.infoObject.uuid).subscribe(zaken => {
            this.zaken = zaken;
        });
    }

    private loadInformatieObject() {
        this.informatieObjectenService.getEnkelvoudigInformatieObject(this.infoObject.uuid)
            .subscribe(informatieObject => {
                this.infoObject = informatieObject;
            });
    }

}
