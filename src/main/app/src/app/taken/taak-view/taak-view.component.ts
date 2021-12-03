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
import {IdentityService} from '../../identity/identity.service';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {FormConfigBuilder} from '../../shared/material-form-builder/model/form-config-builder';
import {Medewerker} from '../../identity/model/medewerker';
import {NavigationService} from '../../shared/navigation/navigation.service';

@Component({
    templateUrl: './taak-view.component.html',
    styleUrls: ['./taak-view.component.less']
})
export class TaakViewComponent extends AbstractView implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    taak: Taak;
    menu: MenuItem[] = [];
    taakBehandelaarFormField;
    toelichtingFormfield;

    formulier: AbstractFormulier;
    formConfig: FormConfig;
    private taakListener: WebsocketListener;

    get taakRechten(): typeof TaakRechten {
        return TaakRechten;
    }

    constructor(store: Store<State>, private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private websocketService: WebsocketService, private taakFormulierenService: TaakFormulierenService, private identityService: IdentityService,
                private navigation: NavigationService) {
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
        this.taakListener = this.websocketService.addListener(Opcode.ANY, ObjectType.TAAK, this.taak.id, this.ophalenTaak);
    }

    ngOnDestroy() {
        super.ngOnDestroy();
        this.websocketService.removeListener(this.taakListener);
    }

    onZaakLoaded($event): void {
        if ($event) {
            this.updateMargins();
        }
    }

    init(taak: Taak): void {
        this.menu = [];
        this.taak = taak;

        this.taakBehandelaarFormField = new AutocompleteFormFieldBuilder().id('taakBehandelaar').label('behandelaar')
                                                                          .value(this.taak.behandelaar).optionLabel('naam')
                                                                          .options(this.identityService.listMedewerkersInGroep(this.taak.groep.id)).build();

        this.toelichtingFormfield = new TextareaFormFieldBuilder().id('toelichting').label('toelichting').value(taak.toelichting).build();

        this.formConfig = new FormConfigBuilder().partialText('actie.opslaan').saveText('actie.opslaan.afronden').build();

        this.formulier = this.taakFormulierenService.getFormulierBuilder(this.taak.taakBehandelFormulier).behandelForm(taak).build();

        this.utilService.setTitle('title.taak', {taak: taak.naam});
        this.setupMenu();
    }

    private setupMenu(): void {
        this.menu.push(new HeaderMenuItem('taak'));
    }

    onFormPartial(formGroup: FormGroup): void {
        this.websocketService.suspendListener(this.taakListener);
        this.takenService.updateTaakdata(this.taakdata(formGroup)).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.opgeslagen');
            this.init(taak);
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.takenService.updateTaakdata(this.taakdata(formGroup)).subscribe(taak => {
                this.utilService.openSnackbar('msg.taak.opgeslagen');
                console.log('takenService.complete');
                this.takenService.complete(this.taak).subscribe(taak => {
                    this.utilService.openSnackbar('msg.taak.afgerond');
                    this.navigation.back();
                });
            });
        }
    }

    taakdata(formGroup: FormGroup): Taak {
        const putData: Taak = new Taak();
        putData.id = this.taak.id;
        putData.taakdata = {};
        Object.keys(formGroup.controls).forEach((key) => {
            putData.taakdata[key] = formGroup.controls[key].value;
        });
        return putData;
    }

    assignToMe(): void {
        this.takenService.assignToLoggedOnUser(this.taak).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.behandelaar.naam});
            this.init(taak);
        });
    }

    editBehandelaar(behandelaar: Medewerker): void {
        if (behandelaar) {
            this.taak.behandelaar = behandelaar;
            this.takenService.assign(this.taak).subscribe(taak => {
                this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.behandelaar.naam});
                this.init(taak);
            });
        } else {
            this.vrijgeven();
        }
    }

    editTaak(value: string, field: string): void {
        this.taak[field] = value;
        this.websocketService.suspendListener(this.taakListener);
        this.takenService.update(this.taak).subscribe((taak) => {
            this.init(taak);
        });
    }

    ophalenTaak() {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.takenService.readTaak(data['taak'].id).subscribe(taak => {
                this.init(taak);
            });
        }));
    }

    vrijgeven = (): void => {
        this.taak.behandelaar = null;
        this.takenService.assign(this.taak).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.vrijgegeven');
            this.init(taak);
        });
    };

    afronden = (): void => {
        this.takenService.complete(this.taak).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.afgerond');
            this.init(taak);
        });
    };
}
