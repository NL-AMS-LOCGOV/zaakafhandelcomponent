/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../model/enkelvoudig-informatieobject';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {ActivatedRoute} from '@angular/router';
import {UtilService} from '../../core/service/util.service';
import {ZaakInformatieobject} from '../model/zaak-informatieobject';
import {HrefMenuItem} from '../../shared/side-nav/menu-item/href-menu-item';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {MatTableDataSource} from '@angular/material/table';
import {HistorieRegel} from '../../shared/historie/model/historie-regel';
import {MatSort} from '@angular/material/sort';
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {ViewComponent} from '../../shared/abstract-view/view-component';
import {FileFormat} from '../model/file-format';

@Component({
    templateUrl: './informatie-object-view.component.html',
    styleUrls: ['./informatie-object-view.component.less']
})
export class InformatieObjectViewComponent extends ViewComponent implements OnInit, AfterViewInit, OnDestroy {

    infoObject: EnkelvoudigInformatieobject;
    documentPreviewBeschikbaar: boolean = false;
    menu: MenuItem[];
    zaken: ZaakInformatieobject[];
    historie: MatTableDataSource<HistorieRegel> = new MatTableDataSource<HistorieRegel>();
    historieColumns: string[] = ['datum', 'gebruiker', 'wijziging', 'oudeWaarde', 'nieuweWaarde'];
    fileIconList = [
        { type: 'xlsx', icon: 'fa-file-excel', color: 'green' },
        { type: 'xls', icon: 'fa-file-excel', color: 'green' },
        { type: 'pdf', icon: 'fa-file-pdf', color: 'red' },
        { type: 'jpg', icon: 'fa-file-image' },
        { type: 'png', icon: 'fa-file-image' },
        { type: 'jpeg', icon: 'fa-file-image' },
        { type: 'gif', icon: 'fa-file-image' },
        { type: 'rtf', icon: 'fa-file-image' },
        { type: 'vsd', icon: 'fa-file-image' },
        { type: 'bmp', icon: 'fa-file-image' },
        { type: 'doc', icon: 'fa-file-word', color: 'blue' },
        { type: 'docx', icon: 'fa-file-word', color: 'blue' },
        { type: 'odt', icon: 'fa-file-word', color: 'blue' },
        { type: 'pptx', icon: 'fa-file-powerpoint', color: 'red' },
        { type: 'txt', icon: 'fa-file-lines' }
    ];
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild(MatSort) sort: MatSort;
    private documentListener: WebsocketListener;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                public utilService: UtilService,
                private websocketService: WebsocketService) {
        super();
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.infoObject = data['informatieObject'];
            this.documentPreviewBeschikbaar = this.infoObject.formaat === FileFormat.PDF;
            this.utilService.setTitle('title.document', {document: this.infoObject.identificatie});

            this.documentListener = this.websocketService.addListenerWithSnackbar(Opcode.ANY, ObjectType.ENKELVOUDIG_INFORMATIEOBJECT, this.infoObject.uuid,
                (event) => this.loadInformatieObject(event));

            this.setupMenu();
            this.loadZaken();
            this.loadHistorie();
        }));
    }

    ngAfterViewInit() {
        super.ngAfterViewInit();
        this.historie.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'datum':
                    return item.datumTijd;
                case 'gebruiker' :
                    return item.door;
                default:
                    return item[property];
            }
        };
        this.historie.sort = this.sort;
    }

    ngOnDestroy() {
        this.websocketService.removeListener(this.documentListener);
    }

    private setupMenu(): void {
        this.menu = [
            new HeaderMenuItem('informatieobject'),
            new HrefMenuItem('actie.downloaden', this.informatieObjectenService.getDownloadURL(this.infoObject.uuid), 'save_alt')
        ];
    }

    private loadZaken(): void {
        this.informatieObjectenService.listZaakInformatieobjecten(this.infoObject.uuid).subscribe(zaken => {
            this.zaken = zaken;
        });
    }

    private loadHistorie(): void {
        this.informatieObjectenService.listHistorie(this.infoObject.uuid).subscribe(historie => {
            this.historie.data = historie;
        });
    }

    private loadInformatieObject(event?: ScreenEvent) {
        if (event) {
            console.log('callback loadInformatieObject: ' + event.key);
        }
        this.informatieObjectenService.readEnkelvoudigInformatieobject(this.infoObject.uuid)
            .subscribe(informatieObject => {
                this.infoObject = informatieObject;
            });
    }

    getFileIcon(filename) {
        const extension = filename.split('.').pop();
        const obj = this.fileIconList.filter(row => {
            if (row.type === extension) {
                return true;
            }
        });
        if (obj.length > 0) {
            return obj[0];
        } else {
            return { type: 'unknown', icon: 'fa-file-circle-question' };
        }
    }

}
