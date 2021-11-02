/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Taak} from '../model/taak';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../../core/service/util.service';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {isZaakVerkortCollapsed} from '../../zaken/state/zaak-verkort.reducer';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {MatSnackBar} from '@angular/material/snack-bar';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Operatie} from '../../core/websocket/model/operatie';
import {ObjectType} from '../../core/websocket/model/object-type';
import {TaakRechten} from '../model/taak-rechten';

@Component({
    templateUrl: './taak-view.component.html',
    styleUrls: ['./taak-view.component.less']
})
export class TaakViewComponent extends AbstractView implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    taak: Taak;
    menu: MenuItem[] = [];

    get taakRechten(): typeof TaakRechten {
        return TaakRechten;
    }

    constructor(store: Store<State>, private route: ActivatedRoute, private takenService: TakenService, private titleService: Title, public utilService: UtilService,
                private snackbar: MatSnackBar, private websocketService: WebsocketService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.init(data['taak']);
        }));
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        this.subscriptions$.push(
            this.store.select(isZaakVerkortCollapsed).subscribe(() => setTimeout(() => this.updateMargins())));
        this.websocketService.addListener(Operatie.WIJZIGING, ObjectType.TAAK, this.taak.id, () => this.ophalenTaak());
    }

    ngOnDestroy() {
        super.ngOnDestroy();
        this.websocketService.removeListeners(Operatie.WIJZIGING, ObjectType.TAAK, this.taak.id);
    }

    onZaakLoaded($event): void {
        if ($event) {
            this.updateMargins();
        }
    }

    init(taak: Taak): void {
        this.menu = [];
        this.taak = taak;
        this.titleService.setTitle(`${taak.naam} | Taakgegevens`);
        this.utilService.setHeaderTitle(`${taak.naam} | Taakgegevens`);
        this.setupMenu();
    }

    private setupMenu(): void {
        this.menu.push(new HeaderMenuItem('Taak'));

        if (this.taak.rechten[this.taakRechten.TOEKENNEN]) {
            this.menu.push(new LinkMenuTitem('Toekennen', `/taken/${this.taak.id}/toekennen`, 'assignment_ind'));
        }

        if (this.taak.rechten[this.taakRechten.VRIJGEVEN]) {
            this.menu.push(new ButtonMenuItem('Vrijgeven', this.vrijgeven, 'assignment_return'));
        }

        if (this.taak.rechten[this.taakRechten.BEHANDELEN]) {
            this.menu.push(new ButtonMenuItem('Afronden', this.afronden, 'assignment_turned_in'));
        }
    }

    ophalenTaak() {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.takenService.getTaak(data['taak'].id).subscribe(taak => {
                this.init(taak);
            });
        }));
    }

    vrijgeven = (): void => {
        this.taak.behandelaar = null;
        this.takenService.toekennen(this.taak).subscribe(taak => {
            this.laatSnackbarZien(`Taak vrijgegeven`);
            this.init(taak);
        });
    };

    afronden = (): void => {
        this.takenService.afronden(this.taak).subscribe(taak => {
            this.laatSnackbarZien(`Taak afgerond`);
            this.init(taak);
        });
    };

    laatSnackbarZien(message: string) {
        this.snackbar.open(message, 'Sluit', {
            duration: 3000
        });
    }
}
