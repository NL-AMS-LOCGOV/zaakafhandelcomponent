/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {Taak} from '../model/taak';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {IdentityService} from '../../identity/identity.service';
import {Medewerker} from '../../identity/model/medewerker';
import {TaakStatus} from '../model/taak-status.enum';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {isZaakVerkortCollapsed} from '../../zaken/state/zaak-verkort.reducer';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';

@Component({
    templateUrl: './taak-view.component.html',
    styleUrls: ['./taak-view.component.less']
})
export class TaakViewComponent extends AbstractView implements OnInit, AfterViewInit {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    taak: Taak;
    menu: MenuItem[] = [];

    constructor(store: Store<State>, private route: ActivatedRoute, private takenService: TakenService, private titleService: Title, public utilService: UtilService,
                private identityService: IdentityService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            this.init(data['taak']);
        });
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        this.store.select(isZaakVerkortCollapsed).subscribe(() => setTimeout(() => this.updateMargins()));
    }

    onZaakLoaded($event): void {
        if ($event) {
            this.updateMargins();
        }
    }

    private init(taak: Taak): void {
        this.menu = [];
        this.taak = taak;
        this.titleService.setTitle(`${taak.naam} | Taakgegevens`);
        this.utilService.setHeaderTitle(`${taak.naam} | Taakgegevens`);
        this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => this.buildMenu(ingelogdeMedewerker));
    }

    private buildMenu(ingelogdeMedewerker: Medewerker): void {
        this.menu.push(new HeaderMenuItem('Taak'));
        if (this.taak.status == TaakStatus.NietToegekend && ingelogdeMedewerker.groepen?.map(groep => groep.id).includes(this.taak.groep.id)) {
            this.menu.push(new ButtonMenuItem('Ken toe aan mijzelf', this.toekennenAanIngelogdeGebruiker, 'person_add_alt'));
            this.menu.push(new LinkMenuTitem('Toekennen', `/taken/${this.taak.id}/toekennen`, 'assignment_ind'));
        } else if (this.taak.status == TaakStatus.Toegekend && ingelogdeMedewerker.gebruikersnaam == this.taak.behandelaar?.gebruikersnaam) {
            this.menu.push(new ButtonMenuItem('Vrijgeven', this.vrijgeven, 'assignment_return'));
            this.menu.push(new LinkMenuTitem('Toekennen', `/taken/${this.taak.id}/toekennen`, 'assignment_ind'));
            this.menu.push(new ButtonMenuItem('Afronden', this.afronden, 'assignment_turned_in'));
        } else if (this.taak.status == TaakStatus.Toegekend && ingelogdeMedewerker.gebruikersnaam != this.taak.behandelaar?.gebruikersnaam) {
            this.menu.push(new ButtonMenuItem('Ken toe aan mijzelf', this.toekennenAanIngelogdeGebruiker, 'person_add_alt'));
        }
    }

    vrijgeven = (): void => {
        this.taak.behandelaar = null;
        this.takenService.toekennen(this.taak).subscribe(() => {
            this.taak.status = TaakStatus.NietToegekend;
            this.init(this.taak)
        });
    };

    afronden = (): void => {
        this.takenService.afronden(this.taak).subscribe(taak => this.init(taak));
    };

    toekennenAanIngelogdeGebruiker = (): void => {
        this.takenService.toekennenAanIngelogdeGebruiker(this.taak).subscribe(medewerker => {
            this.taak.behandelaar = medewerker;
            this.taak.status = TaakStatus.Toegekend;
            this.init(this.taak);
        });
    };
}
