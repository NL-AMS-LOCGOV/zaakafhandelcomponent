/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../model/enkelvoudig-informatieobject';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ZakenService} from '../../zaken/zaken.service';
import {InformatieObjectenService} from '../informatie-objecten.service';
import {ActivatedRoute, Router} from '@angular/router';
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
import {FileFormatUtil} from '../model/file-format';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {SideNavAction} from '../../shared/side-nav/side-nav-action';
import {InformatieobjectStatus} from '../model/informatieobject-status.enum';
import {ActionsViewComponent} from '../../shared/abstract-view/actions-view-component';
import {EnkelvoudigInformatieObjectVersieGegevens} from '../model/enkelvoudig-informatie-object-versie-gegevens';
import {TranslateService} from '@ngx-translate/core';
import {FileIcon} from '../model/file-icon';

@Component({
    templateUrl: './informatie-object-view.component.html',
    styleUrls: ['./informatie-object-view.component.less']
})
export class InformatieObjectViewComponent  extends ActionsViewComponent implements OnInit, AfterViewInit, OnDestroy {

    infoObject: EnkelvoudigInformatieobject;
    laatsteVersieInfoObject: EnkelvoudigInformatieobject;
    documentNieuweVersieGegevens: EnkelvoudigInformatieObjectVersieGegevens;
    documentPreviewBeschikbaar: boolean = false;
    menu: MenuItem[];
    action: string;
    versieInformatie: string;
    zaakInformatieObjecten: ZaakInformatieobject[];
    historie: MatTableDataSource<HistorieRegel> = new MatTableDataSource<HistorieRegel>();
    historieColumns: string[] = ['datum', 'gebruiker', 'wijziging', 'oudeWaarde', 'nieuweWaarde', 'toelichting'];

    @ViewChild('actionsSidenav') actionsSidenav: MatSidenav;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild(MatSort) sort: MatSort;
    private documentListener: WebsocketListener;

    constructor(private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                private route: ActivatedRoute,
                public utilService: UtilService,
                private websocketService: WebsocketService,
                private router: Router,
                private translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.infoObject = data['informatieObject'];
            this.informatieObjectenService.readEnkelvoudigInformatieobject(this.infoObject.uuid).subscribe(eiObject => {
                this.laatsteVersieInfoObject = eiObject;
                this.updateVersieInformatie();
                this.loadZaakInformatieobjecten();
            });
            this.documentPreviewBeschikbaar = FileFormatUtil.isPreviewAvailable(this.infoObject.formaat);
            this.utilService.setTitle('title.document', {document: this.infoObject.identificatie});

            this.documentListener = this.websocketService.addListener(Opcode.ANY, ObjectType.ENKELVOUDIG_INFORMATIEOBJECT, this.infoObject.uuid,
                (event) => {
                    this.loadInformatieObject(event);
                    this.loadHistorie();
                    this.loadZaakInformatieobjecten();
                });

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

    private toevoegenActies() {
        this.menu = [
            new HeaderMenuItem('informatieobject'),
            new HrefMenuItem('actie.downloaden',
                this.informatieObjectenService.getDownloadURL(this.infoObject.uuid, this.infoObject.versie),
                'save_alt')
        ];
        // Nieuwe versie en bewerken acties niet toegestaan indien de status definitief is
        // en wanneer er geen zaak gekoppeld is bij bijvoorbeeld ontkoppelde en inbox documenten.
        // ToDo: Vervangen door Policy
        if (this.laatsteVersieInfoObject.status !== InformatieobjectStatus.DEFINITIEF && this.zaakInformatieObjecten?.length > 0) {
            this.menu.push(new ButtonMenuItem('actie.nieuwe.versie.toevoegen', () => {
                this.informatieObjectenService.readHuidigeVersieEnkelvoudigInformatieObject(this.infoObject.uuid).subscribe(nieuweVersie => {
                    this.documentNieuweVersieGegevens = nieuweVersie;
                    this.actionsSidenav.open();
                    this.action = SideNavAction.DOCUMENT_VERSIE_TOEVOEGEN;
                });
            }, 'difference'));

            this.menu.push(new ButtonMenuItem('actie.bewerken', () => {
                this.informatieObjectenService.editEnkelvoudigInformatieObjectInhoud(this.infoObject.uuid).subscribe(url => {
                    window.open(url);
                });
            }, 'edit'));
        }
    }

    private loadZaakInformatieobjecten(): void {
        this.informatieObjectenService.listZaakInformatieobjecten(this.infoObject.uuid).subscribe(zaakInformatieObjecten => {
            this.zaakInformatieObjecten = zaakInformatieObjecten;
            this.toevoegenActies();
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
                this.laatsteVersieInfoObject = informatieObject;
                this.updateVersieInformatie();
            });
    }

    haalVersieOp(versie: number) {
        this.websocketService.removeListener(this.documentListener);
        this.router.navigate(['/informatie-objecten/', this.infoObject.uuid, versie]);
    }

    versieToegevoegd(informatieobject: EnkelvoudigInformatieobject): void {
        this.haalVersieOp(informatieobject.versie);
    }

    getFileIcon(filename) {
        const extension = filename.split('.').pop();
        const obj = FileIcon.fileIconList.filter(row => {
            if (row.type === extension) {
                return true;
            }
        });
        if (obj.length > 0) {
            return obj[0];
        } else {
            return {type: 'unknown', icon: 'fa-file-circle-question'};
        }
    }

    private updateVersieInformatie(): void {
        this.versieInformatie = this.translate.instant('versie.x.van', {
            versie: this.infoObject.versie,
            laatsteVersie: this.laatsteVersieInfoObject.versie
        });
    }
}
