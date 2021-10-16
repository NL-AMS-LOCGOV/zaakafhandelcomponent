/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Taak} from '../../taken/model/taak';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../../taken/taken.service';
import {EnkelvoudigInformatieObject} from '../../informatie-objecten/model/enkelvoudig-informatie-object';
import {Zaak} from '../model/zaak';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {PlanItemType} from '../../plan-items/model/plan-item-type.enum';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {ZakenService} from '../zaken.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Operatie} from '../../core/websocket/model/operatie';
import {ObjectType} from '../../core/websocket/model/object-type';
import {NotitieType} from '../../shared/notities/model/notitietype.enum';
import {ThemePalette} from '@angular/material/core';

@Component({
    templateUrl: './zaak-view.component.html',
    styleUrls: ['./zaak-view.component.less']
})
export class ZaakViewComponent extends AbstractView implements OnInit, AfterViewInit {

    zaak: Zaak;
    menu: MenuItem[];
    takenDataSource: MatTableDataSource<Taak> = new MatTableDataSource<Taak>();
    toonAfgerondeTaken: boolean = false;

    takenColumnsToDisplay: string[] = ['naam', 'status', 'creatiedatumTijd', 'streefdatum', 'groep', 'behandelaar', 'id'];
    enkelvoudigInformatieObjecten: EnkelvoudigInformatieObject[] = [];
    gerelateerdeZaakColumns: string[] = ['identificatie', 'relatieType', 'omschrijving', 'startdatum', 'einddatum', 'uuid'];
    ingelogdeMedewerker: Medewerker;

    notitieType = NotitieType.ZAAK;

    filterValues: any = {};

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;
    @ViewChild(MatSort) sort: MatSort;

    constructor(store: Store<State>,
                private informatieObjectenService: InformatieObjectenService,
                private takenService: TakenService,
                private zakenService: ZakenService,
                private planItemsService: PlanItemsService,
                private route: ActivatedRoute,
                private titleService: Title,
                private identityService: IdentityService,
                public utilService: UtilService,
                public websocketService: WebsocketService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
        this.route.data.subscribe(data => {
            this.zaak = data['zaak'];
            this.websocketService.addListener(Operatie.WIJZIGING, ObjectType.ZAAK, this.zaak.uuid, event => this.updateZaak());
            this.websocketService.addListener(Operatie.WIJZIGING, ObjectType.ZAAK_TAKEN, this.zaak.uuid, event => this.loadTaken());
            this.websocketService.addListener(Operatie.WIJZIGING, ObjectType.ZAAK_DOCUMENTEN, this.zaak.uuid, event => this.loadInformatieObjecten());

            this.titleService.setTitle(`${this.zaak.identificatie} | Zaakgegevens`);
            this.utilService.setHeaderTitle(`${this.zaak.identificatie} | Zaakgegevens`);
            this.setupMenu();
            this.loadTaken();
            this.loadInformatieObjecten();
        });

        this.takenDataSource.filterPredicate = (data: Taak, filter: string): boolean => {
            const filterValues = JSON.parse(filter);
            return (!this.toonAfgerondeTaken ? data.status !== filterValues.status : true);
        };
    }

    ngAfterViewInit() {
        super.ngAfterViewInit();
        this.takenDataSource.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'groep':
                    return item.groep.naam;
                case 'behandelaar' :
                    return item.behandelaar.naam;
                default:
                    return item[property];
            }
        };
        this.takenDataSource.sort = this.sort;

    }

    private createMenuItem(planItem: PlanItem): MenuItem {
        let icon: string;
        switch (planItem.type) {
            case PlanItemType.HumanTask:
                icon = 'assignment';
                break;
            case PlanItemType.UserEventListener:
                icon = 'fact_check';
                break;
            case PlanItemType.ProcessTask:
                icon = 'launch';
                break;
        }
        return new LinkMenuTitem(planItem.naam, `/plan-items/${planItem.id}/do`, icon);
    }

    private setupMenu(): void {
        this.menu = [];
        this.menu.push(new ButtonMenuItem('Ken toe aan mijzelf', this.zaakToekennenAanIngelogdeGebruiker, 'person_add_alt'));
        if (this.zitIngelogdeMedewerkerInGroepVanZaak() && !this.zaak.behandelaar) {
            this.menu.push(new LinkMenuTitem('Toekennen', `/zaken/${this.zaak.uuid}/toekennen`, 'assignment_ind'));
        } else if (this.zitIngelogdeMedewerkerInGroepVanZaak() && this.isIngelogdeMedewerkerBehandelaar()) {
            this.menu = [
                new HeaderMenuItem('Zaak'),
                new LinkMenuTitem('Document toevoegen', `/informatie-objecten/create/${this.zaak.uuid}`, 'upload_file')
            ];
            this.planItemsService.getPlanItemsForZaak(this.zaak.uuid).subscribe(planItems => {
                if (planItems.length > 0) {
                    this.menu.push(new HeaderMenuItem('Plan items'));
                }
                this.menu = this.menu.concat(planItems.map(planItem => this.createMenuItem(planItem)));
            });
        }
        if (this.isIngelogdeMedewerkerBehandelaar()) {
            this.menu.push(new ButtonMenuItem('Vrijgeven', this.vrijgeven, 'assignment_return'));
            this.menu.push(new LinkMenuTitem('Toekennen', `/zaken/${this.zaak.uuid}/toekennen`, 'assignment_ind'));
        }
    }

    private updateZaak(): void {
        this.zakenService.getZaak(this.zaak.uuid).subscribe(zaak => {
            this.zaak = zaak;
            this.setupMenu();
        });
    }

    private loadInformatieObjecten(): void {
        this.informatieObjectenService.getEnkelvoudigInformatieObjectenVoorZaak(this.zaak.uuid).subscribe(objecten => {
            this.enkelvoudigInformatieObjecten = objecten;
        });
    }

    private loadTaken(): void {
        this.takenService.getTakenVoorZaak(this.zaak.uuid).subscribe(taken => {
            taken = taken.sort((a, b) => a.streefdatum?.localeCompare(b.streefdatum) || a.creatiedatumTijd?.localeCompare(b.creatiedatumTijd));
            this.takenDataSource.data = taken;
            this.filterTakenOpStatus();
        });
    }

    private zitIngelogdeMedewerkerInGroepVanZaak(): boolean {
        return this.ingelogdeMedewerker.groepen.some(g => g.id === this.zaak.groep?.id);
    }

    private isIngelogdeMedewerkerBehandelaar(): boolean {
        return this.ingelogdeMedewerker.gebruikersnaam == this.zaak.behandelaar?.gebruikersnaam;
    }

    vrijgeven = (): void => {
        this.zaak.behandelaar = null;
        this.zakenService.toekennen(this.zaak).subscribe(() => this.ngOnInit());
    };

    zaakToekennenAanIngelogdeGebruiker = (): void => {
        this.zaak.behandelaar = this.ingelogdeMedewerker;
        this.zakenService.toekennenAanIngelogdeGebruiker(this.zaak).subscribe(() => this.updateZaak());
    };

    taakToekennenAanIngelogdeGebruiker(taak: Taak) {
        this.takenService.toekennenAanIngelogdeGebruiker(taak).subscribe(response => {
            taak.behandelaar = response.behandelaar;
            taak.status = response.status;
        });
    }

    filterTakenOpStatus() {
        if (!this.toonAfgerondeTaken) {
            this.filterValues['status'] = 'AFGEROND';
        }

        this.takenDataSource.filter = JSON.stringify(this.filterValues);
    }

    bepaalChipKleur(taak: Taak): ThemePalette {
        if (taak.status === 'AFGEROND') {
            return 'accent';
        } else if (taak.status === 'TOEGEKEND') {
            return 'primary';
        } else {
            return undefined;
        }
    }

    bepaalChipSelected(taak: Taak): boolean {
        return taak.status === 'AFGEROND' || taak.status === 'TOEGEKEND';
    }
}
