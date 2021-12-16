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
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {TaakStatus} from '../model/taak-status.enum';
import {Groep} from '../../identity/model/groep';

@Component({
    templateUrl: './taak-view.component.html',
    styleUrls: ['./taak-view.component.less']
})
export class TaakViewComponent extends AbstractView implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;

    taak: Taak;
    menu: MenuItem[] = [];

    editFormFields: Map<string, any> = new Map<string, any>();

    formulier: AbstractFormulier;
    formConfig: FormConfig;
    private taakListener: WebsocketListener;
    private ingelogdeMedewerker: Medewerker;

    constructor(store: Store<State>, private route: ActivatedRoute, private takenService: TakenService, public utilService: UtilService,
                private websocketService: WebsocketService, private taakFormulierenService: TaakFormulierenService, private identityService: IdentityService,
                private navigation: NavigationService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.getIngelogdeMedewerker();
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.init(data['taak']);
        }));
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();
        this.subscriptions$.push(
            this.store.select(isZaakVerkortCollapsed).subscribe(() => setTimeout(() => this.updateMargins())));
        this.taakListener = this.websocketService.addListener(Opcode.ANY, ObjectType.TAAK, this.taak.id,
            (event) => this.ophalenTaak(event));
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

        this.setEditableFormFields();

        this.createTaakForm(taak);

        this.utilService.setTitle('title.taak', {taak: taak.naam});
        this.setupMenu();
    }

    createTaakForm(taak: Taak): void {
        if (TaakStatus.Afgerond !== this.taak.status) {
            this.formConfig = new FormConfigBuilder().partialText('actie.opslaan').saveText('actie.opslaan.afronden').build();
        } else {
            this.formConfig = null;
        }

        this.formulier = this.taakFormulierenService.getFormulierBuilder(this.taak.taakBehandelFormulier).behandelForm(taak).build();
    }

    setEditableFormFields(): void {
        this.editFormFields.set('behandelaar', new AutocompleteFormFieldBuilder().id('behandelaar').label('behandelaar')
                                                                                 .value(this.taak.behandelaar).optionLabel('naam')
                                                                                 .options(this.identityService.listMedewerkers()).build());
        this.editFormFields.set('groep', new AutocompleteFormFieldBuilder().id('groep').label('groep')
                                                                           .value(this.taak.groep).optionLabel('naam')
                                                                           .options(this.identityService.listGroepen()).build());
        this.editFormFields.set('toelichting', new TextareaFormFieldBuilder().id('toelichting').label('toelichting').value(this.taak.toelichting).build());
    }

    private setupMenu(): void {
        this.menu.push(new HeaderMenuItem('taak'));
    }

    onFormPartial(formGroup: FormGroup): void {
        this.websocketService.suspendListener(this.taakListener);
        this.takenService.updateTaakdata(this.formulier.getTaak(formGroup)).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.opgeslagen');
            this.init(taak);
        });
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.websocketService.suspendListener(this.taakListener);

            this.takenService.complete(this.formulier.getTaak(formGroup)).subscribe(taak => {
                this.utilService.openSnackbar('msg.taak.afgerond');
                this.init(taak);
            });
        }
    }

    assignToMe(): void {
        this.takenService.assignToLoggedOnUser(this.taak).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.behandelaar.naam});
            this.init(taak);
        });
    }

    editGroep(groep: Groep): void {
        this.taak.groep = groep;

        this.takenService.assignGroup(this.taak).subscribe(taak => {
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.groep.naam});
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

    private ophalenTaak(event?: ScreenEvent) {
        if (event) {
            console.log('callback ophalenTaak: ' + event.key);
        }
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

    private getIngelogdeMedewerker() {
        this.identityService.readIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    showAssignToMe(): boolean {
        return this.ingelogdeMedewerker.gebruikersnaam != this.taak.behandelaar?.gebruikersnaam;
    }

    isAfgerond() {
        return this.taak.status == TaakStatus.Afgerond;
    }
}
