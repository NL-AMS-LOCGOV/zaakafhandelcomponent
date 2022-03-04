/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Taak} from '../../taken/model/taak';
import {UtilService} from '../../core/service/util.service';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../../taken/taken.service';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {Zaak} from '../model/zaak';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {PlanItemType} from '../../plan-items/model/plan-item-type.enum';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {ZakenService} from '../zaken.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {NotitieType} from '../../notities/model/notitietype.enum';
import {SessionStorageService} from '../../shared/storage/session-storage.service';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {HistorieRegel} from '../../shared/historie/model/historie-regel';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {Medewerker} from '../../identity/model/medewerker';
import {AutocompleteFormFieldBuilder} from '../../shared/material-form-builder/form-components/autocomplete/autocomplete-form-field-builder';
import {IdentityService} from '../../identity/identity.service';
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {MatDialog} from '@angular/material/dialog';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {ConfirmDialogComponent} from '../../shared/confirm-dialog/confirm-dialog.component';
import {ConfirmDialogData} from '../../shared/confirm-dialog/confirm-dialog-data';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {TaakStatus} from '../../taken/model/taak-status.enum';
import {TranslateService} from '@ngx-translate/core';
import {PersonenService} from '../../personen/personen.service';
import {PersoonOverzicht} from '../../personen/model/persoon-overzicht';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
import {Persoon} from '../../personen/model/persoon';
import {ActionsViewComponent} from '../../shared/abstract-view/actions-view-component';

@Component({
    templateUrl: './zaak-view.component.html',
    styleUrls: ['./zaak-view.component.less']
})
export class ZaakViewComponent extends ActionsViewComponent implements OnInit, AfterViewInit, OnDestroy {

    zaak: Zaak;
    menu: MenuItem[];
    takenDataSource: MatTableDataSource<Taak> = new MatTableDataSource<Taak>();
    toonAfgerondeTaken = false;
    actions = {
        GEEN: 'GEEN',
        ZOEK_PERSOON: 'ZOEK_PERSOON'
    };
    action: string;
    takenFilter: any = {};
    takenColumnsToDisplay: string[] = ['naam', 'status', 'creatiedatumTijd', 'streefdatum', 'groep', 'behandelaar', 'id'];
    enkelvoudigInformatieObjecten: EnkelvoudigInformatieobject[] = [];
    historie: MatTableDataSource<HistorieRegel> = new MatTableDataSource<HistorieRegel>();
    historieColumns: string[] = ['datum', 'gebruiker', 'wijziging', 'oudeWaarde', 'nieuweWaarde', 'toelichting'];
    gerelateerdeZaakColumns: string[] = ['identificatie', 'relatieType', 'omschrijving', 'startdatum', 'einddatum', 'uuid'];
    initiatorPersoon: PersoonOverzicht = new PersoonOverzicht();
    notitieType = NotitieType.ZAAK;
    editFormFields: Map<string, any> = new Map<string, any>();
    editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();

    private zaakListener: WebsocketListener;
    private zaakRollenListener: WebsocketListener;
    private zaakTakenListener: WebsocketListener;
    private zaakDocumentenListener: WebsocketListener;
    private ingelogdeMedewerker: Medewerker;

    @ViewChild('actionsSidenav') actionsSidenav: MatSidenav;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;

    @ViewChild(MatSort) sort: MatSort;

    constructor(private informatieObjectenService: InformatieObjectenService,
                private takenService: TakenService,
                private zakenService: ZakenService,
                private identityService: IdentityService,
                private planItemsService: PlanItemsService,
                private personenService: PersonenService,
                private route: ActivatedRoute,
                public utilService: UtilService,
                public websocketService: WebsocketService,
                private sessionStorageService: SessionStorageService,
                public dialog: MatDialog,
                private translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.init(data['zaak']);
            this.zaakListener = this.websocketService.addListenerWithSnackbar(Opcode.ANY, ObjectType.ZAAK, this.zaak.uuid,
                (event) => this.updateZaak(event));
            this.zaakRollenListener = this.websocketService.addListenerWithSnackbar(Opcode.UPDATED, ObjectType.ZAAK_ROLLEN, this.zaak.uuid,
                (event) => this.updateZaak(event));
            this.zaakTakenListener = this.websocketService.addListenerWithSnackbar(Opcode.UPDATED, ObjectType.ZAAK_TAKEN, this.zaak.uuid,
                (event) => this.loadTaken(event));
            this.zaakDocumentenListener = this.websocketService.addListenerWithSnackbar(Opcode.UPDATED, ObjectType.ZAAK_INFORMATIEOBJECTEN, this.zaak.uuid,
                (event) => this.loadInformatieObjecten(event));

            this.utilService.setTitle('title.zaak', {zaak: this.zaak.identificatie});

            this.getIngelogdeMedewerker();
            this.loadTaken();
            this.loadInformatieObjecten();
            if (this.zaak.initiatorBSN) {
                this.loadInitiatorPersoon();
            }
        }));

        this.takenDataSource.filterPredicate = (data: Taak, filter: string): boolean => {
            return (!this.toonAfgerondeTaken ? data.status !== filter['status'] : true);
        };

        this.toonAfgerondeTaken = this.sessionStorageService.getSessionStorage('toonAfgerondeTaken');

    }

    init(zaak: Zaak): void {
        this.zaak = zaak;
        this.loadHistorie();
        this.setEditableFormFields();
        this.setupMenu();
    }

    private getIngelogdeMedewerker() {
        this.identityService.readIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
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

    ngOnDestroy(): void {
        super.ngOnDestroy();
        this.websocketService.removeListener(this.zaakListener);
        this.websocketService.removeListener(this.zaakRollenListener);
        this.websocketService.removeListener(this.zaakTakenListener);
        this.websocketService.removeListener(this.zaakDocumentenListener);
    }

    private setEditableFormFields(): void {
        this.editFormFields.set('behandelaar', new AutocompleteFormFieldBuilder().id('behandelaar').label('behandelaar')
                                                                                 .value(this.zaak.behandelaar).optionLabel('naam')
                                                                                 .options(this.identityService.listMedewerkers()).build());
        this.editFormFields.set('groep', new AutocompleteFormFieldBuilder().id('groep').label('groep')
                                                                           .value(this.zaak.groep).optionLabel('naam')
                                                                           .options(this.identityService.listGroepen()).build());
        this.editFormFields.set('omschrijving', new TextareaFormFieldBuilder().id('omschrijving').label('omschrijving')
                                                                              .value(this.zaak.omschrijving).maxlength(80)
                                                                              .build());
        this.editFormFields.set('toelichting', new TextareaFormFieldBuilder().id('toelichting').label('toelichting')
                                                                             .value(this.zaak.toelichting).maxlength(1000).build());
        this.editFormFields.set('vertrouwelijkheidaanduiding',
            new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding').label('vertrouwelijkheidaanduiding')
                                        .value({
                                            label: this.translate.instant('vertrouwelijkheidaanduiding.' + this.zaak.vertrouwelijkheidaanduiding),
                                            value: this.zaak.vertrouwelijkheidaanduiding
                                        })
                                        .optionLabel('label')
                                        .options(this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
                                            Vertrouwelijkheidaanduiding)).build());
        this.editFormFields.set('startdatum',
            new DateFormFieldBuilder().id('startdatum').label('startdatum').value(this.zaak.startdatum).build());

        this.editFormFields.set('einddatumGepland',
            new DateFormFieldBuilder().id('einddatumGepland').label('einddatumGepland').value(this.zaak.einddatumGepland).build());
        this.editFormFieldIcons.set('einddatumGepland', new TextIcon(Conditionals.isAfterDate(this.zaak.einddatum), 'report_problem', 'warningVerlopen_icon',
            'msg.datum.overschreden', 'warning'));

        this.editFormFields.set('uiterlijkeEinddatumAfdoening',
            new DateFormFieldBuilder().id('uiterlijkeEinddatumAfdoening').label('uiterlijkeEinddatumAfdoening')
                                      .value(this.zaak.uiterlijkeEinddatumAfdoening)
                                      .build());
        this.editFormFieldIcons.set('uiterlijkeEinddatumAfdoening',
            new TextIcon(Conditionals.isAfterDate(this.zaak.einddatum), 'report_problem', 'errorVerlopen_icon',
                'msg.datum.overschreden', 'error'));

        this.editFormFields.set('reden', new InputFormFieldBuilder().id('reden').label('reden').build());
    }

    private createMenuItem(planItem: PlanItem): MenuItem {
        switch (planItem.type) {
            case PlanItemType.HumanTask:
                return new LinkMenuTitem(planItem.naam, `/plan-items/${planItem.id}/do`, 'assignment');
            case PlanItemType.UserEventListener:
                return new ButtonMenuItem(planItem.naam, () => this.openPlanItemStartenDialog(planItem), 'fact_check');
            case PlanItemType.ProcessTask:
                return new ButtonMenuItem(planItem.naam, () => this.openPlanItemStartenDialog(planItem), 'launch');
        }
        throw new Error(`Onbekend type: ${planItem.type}`);
    }

    private setupMenu(): void {
        this.menu = [new HeaderMenuItem('zaak')];

        this.menu.push(new LinkMenuTitem('actie.document.aanmaken', `/informatie-objecten/create/${this.zaak.uuid}`, 'upload_file'));
        if (!this.zaak.initiatorBSN) {
            this.menu.push(new ButtonMenuItem('initiator.toevoegen', () => {
                this.actionsSidenav.open();
                this.action = this.actions.ZOEK_PERSOON;
            }, 'emoji_people'));
        }
        this.planItemsService.listPlanItemsForZaak(this.zaak.uuid).subscribe(planItems => {
            const actieItems: PlanItem[] = planItems.filter(planItem => planItem.type !== PlanItemType.HumanTask);
            const humanTaskItems: PlanItem[] = planItems.filter(planItem => planItem.type === PlanItemType.HumanTask);
            if (humanTaskItems.length > 0) {
                this.menu.push(new HeaderMenuItem('actie.taak.starten'));
                this.menu = this.menu.concat(humanTaskItems.map(planItem => this.createMenuItem(planItem)));
            }
            if (actieItems.length > 0) {
                this.menu.push(new HeaderMenuItem('actie.zaak.acties'));
                this.menu = this.menu.concat(actieItems.map(planItem => this.createMenuItem(planItem)));
            }
        });
    }

    openPlanItemStartenDialog(planItem: PlanItem): void {
        const confirmDialogData = new ConfirmDialogData('actie.planitem.uitvoeren.bevestigen', {planitem: planItem.naam},
            this.planItemsService.doPlanItem(planItem));
        this.dialog.open(ConfirmDialogComponent, {
            width: '400px',
            data: confirmDialogData,
            autoFocus: 'dialog'
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('actie.planitem.uitgevoerd', {planitem: planItem});
                this.updateZaak();
            }
        });
    }

    editDatumGroep(event: any): void {
        const zaak: Zaak = new Zaak();

        zaak.startdatum = event.startdatum;
        zaak.einddatumGepland = event.einddatumGepland;
        zaak.uiterlijkeEinddatumAfdoening = event.uiterlijkeEinddatumAfdoening;

        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.partialUpdateZaak(this.zaak.uuid, zaak, event.reden).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    editGroep(event: any): void {
        this.zaak.groep = event.groep;
        this.doubleSuspendRollenListener();
        this.zakenService.toekennenGroep(this.zaak, event.reden).subscribe(zaak => {
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.groep.naam});
            this.init(zaak);
        });
    }

    editBehandelaar(event: any): void {
        if (event.behandelaar) {
            this.zaak.behandelaar = event.behandelaar;
            this.doubleSuspendRollenListener();
            this.zakenService.toekennen(this.zaak, event.reden).subscribe(zaak => {
                this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar?.naam});
                this.init(zaak);
            });
        } else {
            this.vrijgeven(event.reden);
        }
    }

    private vrijgeven(reden: string): void {
        this.zaak.behandelaar = null;
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.zakenService.vrijgeven([this.zaak], reden).subscribe(() => {
            this.init(this.zaak);
            this.utilService.openSnackbar('msg.zaak.vrijgegeven');
        });
    }

    editZaakMetReden(event: any, field: string): void {
        const zaak: Zaak = new Zaak();
        zaak[field] = event[field];
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.partialUpdateZaak(this.zaak.uuid, zaak, event.reden).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    editZaak(value: string, field: string): void {
        const zaak: Zaak = new Zaak();
        zaak[field] = value;
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.partialUpdateZaak(this.zaak.uuid, zaak).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    private updateZaak(event?: ScreenEvent): void {
        if (event) {
            console.log('callback updateZaak: ' + event.key);
        }
        this.zakenService.readZaak(this.zaak.uuid).subscribe(zaak => {
            this.init(zaak);
        });
    }

    private loadInformatieObjecten(event?: ScreenEvent): void {
        if (event) {
            console.log('callback loadInformatieObjecten: ' + event.key);
        }
        const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekParameters.zaakUUID = this.zaak.uuid;
        this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters).subscribe(objecten => {
            this.enkelvoudigInformatieObjecten = objecten;
        });
    }

    private loadHistorie(): void {
        this.zakenService.listHistorieVoorZaak(this.zaak.uuid).subscribe(historie => {
            this.historie.data = historie;
        });
    }

    private loadTaken(event?: ScreenEvent): void {
        if (event) {
            console.log('callback loadTaken: ' + event.key);
        }
        // TODO #315
        this.websocketService.suspendListener(this.zaakTakenListener);
        this.takenService.listTakenVoorZaak(this.zaak.uuid).subscribe(taken => {
            taken = taken.sort((a, b) => a.streefdatum?.localeCompare(b.streefdatum) ||
                a.creatiedatumTijd?.localeCompare(b.creatiedatumTijd));
            this.takenDataSource.data = taken;
            this.filterTakenOpStatus();
        });
    }

    private loadInitiatorPersoon(): void {
        this.personenService.readPersoonOverzicht(this.zaak.initiatorBSN).subscribe(persoon => {
            this.initiatorPersoon = persoon;
        });
    }

    showAssignToMe(zaakOrTaak: Zaak | Taak): boolean {
        return this.ingelogdeMedewerker.gebruikersnaam !== zaakOrTaak.behandelaar?.gebruikersnaam && zaakOrTaak.status !== TaakStatus.Afgerond;
    }

    assignToMe(event: any): void {
        this.doubleSuspendRollenListener();
        this.zakenService.toekennenAanIngelogdeMedewerker(this.zaak, event.reden).subscribe(zaak => {
            this.init(zaak);
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar?.naam});
        });
    }

    persoonGeselecteerd(persoon: Persoon): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.actionsSidenav.close();
        this.zakenService.createInitiator(this.zaak, persoon.bsn, 'Initiator toegekend door de medewerker tijdens het behandelen van de zaak').subscribe(() => {
            this.zaak.initiatorBSN = persoon.bsn;
            this.initiatorPersoon = persoon;
            this.init(this.zaak);
        });
    }

    assignTaskToMe(taak: Taak) {
        this.websocketService.suspendListener(this.zaakTakenListener);
        this.takenService.assignToLoggedOnUser(taak).subscribe(returnTaak => {
            taak.behandelaar = returnTaak.behandelaar;
            taak.status = returnTaak.status;
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.behandelaar.naam});
        });
    }

    filterTakenOpStatus() {
        if (!this.toonAfgerondeTaken) {
            this.takenFilter['status'] = 'AFGEROND';
        }

        this.takenDataSource.filter = this.takenFilter;
        this.sessionStorageService.setSessionStorage('toonAfgerondeTaken', this.toonAfgerondeTaken);
    }

    private doubleSuspendRollenListener() {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.websocketService.suspendListener(this.zaakRollenListener);
    }
}
