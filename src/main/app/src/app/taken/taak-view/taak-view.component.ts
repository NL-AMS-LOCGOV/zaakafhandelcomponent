/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Taak} from '../model/taak';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {UtilService} from '../../core/service/util.service';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {isZaakVerkortCollapsed} from '../../zaken/state/zaak-verkort.reducer';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {TaakRechten} from '../model/taak-rechten';
import {FormGroup} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {AbstractFormulier} from '../../formulieren/model/abstract-formulier';
import {TaakFormulierenService} from '../../formulieren/taak-formulieren.service';

@Component({
    templateUrl: './taak-view.component.html',
    styleUrls: ['./taak-view.component.less']
})
export class TaakViewComponent extends AbstractView implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    taak: Taak;
    menu: MenuItem[] = [];

    formulier: AbstractFormulier;
    formConfig: FormConfig;

    get taakRechten(): typeof TaakRechten {
        return TaakRechten;
    }

    constructor(store: Store<State>, private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private websocketService: WebsocketService, private taakFormulierenService: TaakFormulierenService) {
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
        this.websocketService.addListener(Opcode.UPDATED, ObjectType.TAAK, this.taak.id, () => this.ophalenTaak());
        this.websocketService.addListener(Opcode.DELETED, ObjectType.TAAK, this.taak.id, () => this.ophalenTaak());
    }

    ngOnDestroy() {
        super.ngOnDestroy();
        this.websocketService.removeListeners(Opcode.UPDATED, ObjectType.TAAK, this.taak.id);
        this.websocketService.removeListeners(Opcode.DELETED, ObjectType.TAAK, this.taak.id);
    }

    onZaakLoaded($event): void {
        if ($event) {
            this.updateMargins();
        }
    }

    init(taak: Taak): void {
        this.menu = [];
        this.taak = taak;
        this.formConfig = new FormConfig('actie.afronden');

        this.formulier = this.taakFormulierenService.getFormulierBuilder(this.taak.taakBehandelFormulier).behandelForm(taak).build();

        this.utilService.setTitle('title.taak', {taak: taak.naam});
        this.setupMenu();
    }

    private setupMenu(): void {
        this.menu.push(new HeaderMenuItem('taak'));

        if (this.taak.rechten[this.taakRechten.TOEKENNEN]) {
            this.menu.push(new LinkMenuTitem('actie.toekennen', `/taken/${this.taak.id}/toekennen`, 'assignment_ind'));
        }

        if (this.taak.rechten[this.taakRechten.VRIJGEVEN]) {
            this.menu.push(new ButtonMenuItem('actie.vrijgeven', this.vrijgeven, 'assignment_return'));
        }

        if (this.taak.rechten[this.taakRechten.BEHANDELEN]) {
            this.menu.push(new ButtonMenuItem('actie.afronden', this.afronden, 'assignment_turned_in'));
        }
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            let taak1: Taak = this.formulier.getTaak(formGroup);
            console.log(taak1);
        }
    }

    editTaak(value: string, field: string): void {
        this.taak[field] = value;
        this.takenService.bewerken(this.taak).subscribe((taak) => {
            this.init(taak);
        });
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
            this.utilService.openSnackbar('msg.taak.vrijgegeven');
            this.init(taak);
        });
    };

    afronden = (): void => {
        this.takenService.afronden(this.taak).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.afgerond');
            this.init(taak);
        });
    };
}
