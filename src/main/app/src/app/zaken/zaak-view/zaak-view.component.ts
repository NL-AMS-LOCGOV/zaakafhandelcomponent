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
import {Zaak} from '../model/zaak';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {PlanItemType} from '../../plan-items/model/plan-item-type.enum';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {ZakenService} from '../zaken.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {NotitieType} from '../../notities/model/notitietype.enum';
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
import {DialogComponent} from '../../shared/dialog/dialog.component';
import {DialogData} from '../../shared/dialog/dialog-data';
import {TaakStatus} from '../../taken/model/taak-status.enum';
import {TranslateService} from '@ngx-translate/core';
import {KlantenService} from '../../klanten/klanten.service';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
import {ActionsViewComponent} from '../../shared/abstract-view/actions-view-component';
import {Validators} from '@angular/forms';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';
import {ZaakbeeindigReden} from '../../admin/model/zaakbeeindig-reden';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../shared/confirm-dialog/confirm-dialog.component';
import {Klant} from '../../klanten/model/klant';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {AddressResult, LocationService} from '../../shared/location/location.service';
import {GeometryType} from '../model/geometryType';
import {SideNavAction} from '../../shared/side-nav/side-nav-action';
import {LocationUtil} from '../../shared/location/location-util';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {UserEventListenerActie} from '../../plan-items/model/user-event-listener-actie-enum';
import {UserEventListenerData} from '../../plan-items/model/user-event-listener-data';
import {Observable} from 'rxjs';

@Component({
    templateUrl: './zaak-view.component.html',
    styleUrls: ['./zaak-view.component.less']
})
export class ZaakViewComponent extends ActionsViewComponent implements OnInit, AfterViewInit, OnDestroy {

    zaak: Zaak;
    zaakLocatie: AddressResult;
    actiefPlanItem: PlanItem;
    menu: MenuItem[];
    takenDataSource: MatTableDataSource<Taak> = new MatTableDataSource<Taak>();
    toonAfgerondeTaken = false;
    action: string;
    takenFilter: any = {};
    takenColumnsToDisplay: string[] = ['naam', 'status', 'creatiedatumTijd', 'streefdatum', 'groep', 'behandelaar', 'id'];
    toegevoegdDocument: EnkelvoudigInformatieobject;

    historie: MatTableDataSource<HistorieRegel> = new MatTableDataSource<HistorieRegel>();
    historieColumns: string[] = ['datum', 'gebruiker', 'wijziging', 'oudeWaarde', 'nieuweWaarde', 'toelichting'];
    gerelateerdeZaakColumns: string[] = ['identificatie', 'relatieType', 'omschrijving', 'startdatum', 'einddatum', 'uuid'];
    notitieType = NotitieType.ZAAK;
    editFormFields: Map<string, any> = new Map<string, any>();
    editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();
    viewInitialized = false;
    toolTipIcon = new TextIcon(Conditionals.always, 'help_outline', 'toolTip_icon', '', 'pointer');
    locatieIcon = new TextIcon(Conditionals.always, 'place', 'locatie_icon', '', 'pointer');

    private zaakListener: WebsocketListener;
    private zaakRollenListener: WebsocketListener;
    private zaakTakenListener: WebsocketListener;
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
                private klantenService: KlantenService,
                private zaakafhandelParametersService: ZaakafhandelParametersService,
                private route: ActivatedRoute,
                private utilService: UtilService,
                private websocketService: WebsocketService,
                private dialog: MatDialog,
                private translate: TranslateService,
                private locationService: LocationService) {
        super();
    }

    ngOnInit(): void {

        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.init(data['zaak']);
            this.zaakListener = this.websocketService.addListenerWithSnackbar(Opcode.ANY, ObjectType.ZAAK, this.zaak.uuid,
                (event) => this.updateZaak(event));
            this.zaakRollenListener = this.websocketService.addListenerWithSnackbar(Opcode.UPDATED, ObjectType.ZAAK_ROLLEN, this.zaak.uuid,
                (event) => this.updateZaak(event));
            this.zaakTakenListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_TAKEN, this.zaak.uuid,
                (event) => this.loadTaken(event));

            this.utilService.setTitle('title.zaak', {zaak: this.zaak.identificatie});

            this.getIngelogdeMedewerker();
            this.loadTaken();
        }));

        this.takenDataSource.filterPredicate = (data: Taak, filter: string): boolean => {
            return (!this.toonAfgerondeTaken ? data.status !== filter['status'] : true);
        };

        this.toonAfgerondeTaken = SessionStorageUtil.getItem('toonAfgerondeTaken');

    }

    init(zaak: Zaak): void {
        this.zaak = zaak;
        this.loadHistorie();
        this.setEditableFormFields();
        this.setupMenu();
        this.loadLocatie();
    }

    private getIngelogdeMedewerker() {
        this.identityService.readIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
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
    }

    private setEditableFormFields(): void {
        this.editFormFields.set('communicatiekanaal',
            new SelectFormFieldBuilder().id('communicatiekanaal').label('communicatiekanaal')
                                        .value(this.zaak.communicatiekanaal)
                                        .optionLabel('naam')
                                        .options(this.zakenService.listCommunicatiekanalen()).build());

        this.editFormFields.set('behandelaar', new AutocompleteFormFieldBuilder().id('behandelaar').label('behandelaar')
                                                                                 .value(this.zaak.behandelaar)
                                                                                 .optionLabel('naam')
                                                                                 .options(
                                                                                     this.identityService.listMedewerkers())
                                                                                 .build());
        this.editFormFields.set('groep', new AutocompleteFormFieldBuilder().id('groep').label('groep')
                                                                           .value(this.zaak.groep).optionLabel('naam')
                                                                           .options(this.identityService.listGroepen())
                                                                           .build());
        this.editFormFields.set('omschrijving', new TextareaFormFieldBuilder().id('omschrijving').label('omschrijving')
                                                                              .value(this.zaak.omschrijving)
                                                                              .maxlength(80)
                                                                              .build());
        this.editFormFields.set('toelichting', new TextareaFormFieldBuilder().id('toelichting').label('toelichting')
                                                                             .value(this.zaak.toelichting)
                                                                             .maxlength(1000).build());
        this.editFormFields.set('vertrouwelijkheidaanduiding',
            new SelectFormFieldBuilder().id('vertrouwelijkheidaanduiding').label('vertrouwelijkheidaanduiding')
                                        .value({
                                            label: this.translate.instant(
                                                'vertrouwelijkheidaanduiding.' + this.zaak.vertrouwelijkheidaanduiding),
                                            value: 'vertrouwelijkheidaanduiding.' + this.zaak.vertrouwelijkheidaanduiding
                                        })
                                        .optionLabel('label')
                                        .options(this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
                                            Vertrouwelijkheidaanduiding)).build());

        this.editFormFields.set('startdatum',
            new DateFormFieldBuilder().id('startdatum').label('startdatum').value(this.zaak.startdatum).build());

        this.editFormFields.set('einddatumGepland',
            new DateFormFieldBuilder().id('einddatumGepland').label('einddatumGepland')
                                      .value(this.zaak.einddatumGepland).build());
        this.editFormFieldIcons.set('einddatumGepland',
            new TextIcon(Conditionals.isAfterDate(this.zaak.einddatum), 'report_problem', 'warningVerlopen_icon',
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
                return new ButtonMenuItem(planItem.naam, () => {
                    if (!this.actiefPlanItem || this.actiefPlanItem.id !== planItem.id) {
                        this.action = null;
                        this.planItemsService.readHumanTask(planItem.id).subscribe(data => {
                            this.actiefPlanItem = data;
                            this.actionsSidenav.open();
                            this.action = SideNavAction.TAAK_STARTEN;
                        });
                    } else {
                        this.action = SideNavAction.TAAK_STARTEN;
                        this.actionsSidenav.open();
                    }
                }, 'assignment');

            case PlanItemType.UserEventListener:
                return new ButtonMenuItem(planItem.naam, () => this.openPlanItemStartenDialog(planItem), 'fact_check');
            case PlanItemType.ProcessTask:
                return new ButtonMenuItem(planItem.naam, () => this.openPlanItemStartenDialog(planItem), 'launch');
        }
        throw new Error(`Onbekend type: ${planItem.type}`);
    }

    private setupMenu(): void {
        this.menu = [new HeaderMenuItem('zaak')];

        this.menu.push(new ButtonMenuItem('actie.document.aanmaken', () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.DOCUMENT_TOEVOEGEN;
        }, 'upload_file'));

        this.menu.push(new ButtonMenuItem('actie.mail.versturen', () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.MAIL_VERSTUREN;
        }, 'mail'));

        if (!this.zaak.ontvangstbevestigingVerstuurd) {
            this.menu.push(new ButtonMenuItem('actie.ontvangstbevestiging.versturen', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.ONTVANGSTBEVESTIGING;
            }, 'mark_email_read'));
        }

        if (this.zaak.rechten.open && this.zaak.rechten.afbreekbaar) {
            this.menu.push(new ButtonMenuItem('actie.zaak.afbreken', () => this.openZaakAfbrekenDialog(), 'exit_to_app'));
        }

        if (!this.zaak.initiatorIdentificatie) {
            this.menu.push(new HeaderMenuItem('initiator.toevoegen'));
            this.menu.push(new ButtonMenuItem('initiator.toevoegen.persoon', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.ZOEK_PERSOON;
            }, 'emoji_people'));
            this.menu.push(new ButtonMenuItem('initiator.toevoegen.bedrijf', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.ZOEK_BEDRIJF;
            }, 'business'));
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
        this.websocketService.doubleSuspendListener(this.zaakListener);
        const melding = this.translate.instant('actie.planitem.uitvoeren.bevestigen', {planitem: planItem.naam});
        const userEventListenerDialog = this.createUserEventListenerDialog(planItem, melding);
        this.dialog.open(userEventListenerDialog.dialogComponent, {
            data: userEventListenerDialog.dialogData
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('actie.planitem.uitgevoerd', {planitem: planItem.naam});
                this.updateZaak();
            }
        });
    }

    createUserEventListenerDialog(planItem: PlanItem, melding: string): { dialogComponent: any, dialogData: any } {
        switch (planItem.userEventListenerActie) {
            case UserEventListenerActie.Ontvankelijk:
                return this.createUserEventListenerOntvankelijkDialog(planItem, melding);
            case UserEventListenerActie.NietOntvankelijk:
                return this.createUserEventListenerNietOntvankelijkDialog(planItem, melding);
            case UserEventListenerActie.Afhandelen:
                return this.createUserEventListenerAfhandelenDialog(planItem, melding);
            default:
                throw new Error(`Niet bestaande UserEventListenerActie: ${planItem.userEventListenerActie}`);
        }
    }

    createUserEventListenerOntvankelijkDialog(planItem: PlanItem, melding: string): { dialogComponent: any, dialogData: any } {
        return {
            dialogComponent: ConfirmDialogComponent,
            dialogData: new ConfirmDialogData(melding, this.doUserEventListenerOntvankelijk(planItem.id), planItem.toelichting)
        };
    }

    private doUserEventListenerOntvankelijk(planItemId: string): Observable<void> {
        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.Ontvankelijk, planItemId, this.zaak.uuid);
        return this.planItemsService.doUserEventListener(userEventListenerData);
    }

    createUserEventListenerNietOntvankelijkDialog(planItem: PlanItem, melding: string): { dialogComponent: any, dialogData: any } {
        return {
            dialogComponent: DialogComponent,
            dialogData: new DialogData(
                new TextareaFormFieldBuilder().id('reden').label('reden').validators(Validators.required).build(),
                (reden: string) => this.doUserEventListenerNietOntvankelijk(planItem.id, reden), melding, planItem.toelichting)
        };
    }

    private doUserEventListenerNietOntvankelijk(planItemId: string, resultaatToelichting: string): Observable<void> {
        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.NietOntvankelijk, planItemId, this.zaak.uuid);
        userEventListenerData.resultaatToelichting = resultaatToelichting;
        return this.planItemsService.doUserEventListener(userEventListenerData);
    }

    createUserEventListenerAfhandelenDialog(planItem: PlanItem, melding: string): { dialogComponent: any, dialogData: any } {
        // ToDo resultaattype en eventueel ook resultaat toelichting moet gekozen kunnen worden in dropdown
        const resultaattypeUuid = '4580d1fb-c5b5-4d5e-b39b-fe8bbfd32516';
        const resultaatToelichting = 'Toelichting op resultaat';
        return {
            dialogComponent: ConfirmDialogComponent,
            dialogData: new ConfirmDialogData(melding, this.doUserEventListenerAfhandelen(planItem.id, resultaattypeUuid, resultaatToelichting),
                planItem.toelichting)
        };
    }

    private doUserEventListenerAfhandelen(planItemId: string, resultaattypeUuid: string, resultaatToelichting: string): Observable<void> {
        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.Afhandelen, planItemId, this.zaak.uuid);
        userEventListenerData.resultaattypeUuid = resultaattypeUuid;
        userEventListenerData.resultaatToelichting = resultaatToelichting;
        return this.planItemsService.doUserEventListener(userEventListenerData);
    }

    openZaakAfbrekenDialog(): void {
        const dialogData = new DialogData(
            new SelectFormFieldBuilder().id('reden')
                                        .label('actie.zaak.afbreken.reden')
                                        .optionLabel('naam')
                                        .options(this.zaakafhandelParametersService.listZaakbeeindigRedenenForZaaktype(this.zaak.zaaktype.uuid))
                                        .validators(Validators.required)
                                        .build(),
            (zaakbeeindigReden: ZaakbeeindigReden) => this.zakenService.afbreken(this.zaak.uuid, zaakbeeindigReden));
        dialogData.confirmButtonActionKey = 'actie.zaak.afbreken';

        this.websocketService.doubleSuspendListener(this.zaakListener);
        this.dialog.open(DialogComponent, {
            data: dialogData
        }).afterClosed().subscribe(result => {
            if (result) {
                this.updateZaak();
                this.loadTaken();
                this.utilService.openSnackbar('actie.zaak.afgebroken');
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
        this.websocketService.doubleSuspendListener(this.zaakRollenListener);
        this.zakenService.toekennenGroep(this.zaak, event.reden).subscribe(zaak => {
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.groep.naam});
            this.init(zaak);
        });
    }

    editBehandelaar(event: any): void {
        if (event.behandelaar) {
            this.zaak.behandelaar = event.behandelaar;
            this.websocketService.doubleSuspendListener(this.zaakRollenListener);
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
        zaak[field] = event[field].value ? event[field].value : event[field];
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

    private loadHistorie(): void {
        this.zakenService.listHistorieVoorZaak(this.zaak.uuid).subscribe(historie => {
            this.historie.data = historie;
        });
    }

    editZaakLocatie(): void {
        this.action = SideNavAction.ZOEK_LOCATIE;
        this.actionsSidenav.open();
    }

    private loadLocatie(): void {
        if (this.zaak.zaakgeometrie) {
            switch (this.zaak.zaakgeometrie.type) {
                case GeometryType.POINT:
                    this.locationService.coordinatesToAddress([this.zaak.zaakgeometrie.point.x, this.zaak.zaakgeometrie.point.y]).subscribe(objectData => {
                        this.zaakLocatie = objectData.response.docs[0];
                    });
                    break;
            }
        }
    }

    takenLoading = false;

    private loadTaken(event?: ScreenEvent): void {
        if (event) {
            this.takenLoading = true;
            console.log('callback loadTaken: ' + event.key);
        }
        // TODO #315
        this.websocketService.suspendListener(this.zaakTakenListener);
        this.takenService.listTakenVoorZaak(this.zaak.uuid).subscribe(taken => {
            taken = taken.sort((a, b) => a.streefdatum?.localeCompare(b.streefdatum) ||
                a.creatiedatumTijd?.localeCompare(b.creatiedatumTijd));
            this.takenDataSource.data = taken;
            this.filterTakenOpStatus();
            this.takenLoading = false;
        });
    }

    showAssignToMe(zaakOrTaak: Zaak | Taak): boolean {
        return this.ingelogdeMedewerker.gebruikersnaam !== zaakOrTaak.behandelaar?.gebruikersnaam && zaakOrTaak.status !== TaakStatus.Afgerond;
    }

    assignToMe(event: any): void {
        this.websocketService.doubleSuspendListener(this.zaakRollenListener);
        this.zakenService.toekennenAanIngelogdeMedewerker(this.zaak, event.reden).subscribe(zaak => {
            this.init(zaak);
            this.utilService.openSnackbar('msg.zaak.toegekend', {behandelaar: zaak.behandelaar?.naam});
        });
    }

    locatieGeselecteerd(locatie: AddressResult): void {
        this.zaakLocatie = locatie;
        this.actionsSidenav.close();

        const zaak: Zaak = new Zaak();
        if (locatie) {
            zaak.zaakgeometrie = LocationUtil.point(locatie.centroide_ll);
        }

        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.partialUpdateZaak(this.zaak.uuid, zaak).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    initiatorGeselecteerd(initiator: Klant): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.actionsSidenav.close();
        this.zakenService.createInitiator(this.zaak, initiator.identificatie)
            .subscribe(() => {
                this.utilService.openSnackbar('msg.initiator.toegevoegd', {naam: initiator.naam});
                this.zaak.initiatorIdentificatie = initiator.identificatie;
                this.setupMenu();
            });
    }

    deleteInitiator(): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('actie.initiator.ontkoppelen.bevestigen'),
                this.zakenService.deleteInitiator(this.zaak)
            )
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('actie.initiator.ontkoppelen.uitgevoerd');
                this.zaak.initiatorIdentificatie = null;
                this.init(this.zaak);
            }
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
        SessionStorageUtil.setItem('toonAfgerondeTaken', this.toonAfgerondeTaken);
    }

    get initiatorType() {
        if (this.zaak.initiatorIdentificatie) {
            if (this.zaak.initiatorIdentificatie.length === 9) {
                return 'PERSOON';
            }
            return 'BEDRIJF';
        }
        return null;
    }

    taakGestart(): void {
        this.actiefPlanItem = null;
        this.actionsSidenav.close();
    }

    mailVerstuurd(): void {
        this.action = null;
        this.actionsSidenav.close();
    }

    ontvangstBevestigd(bevestigd: boolean): void {
        this.action = null;
        this.actionsSidenav.close();
        this.zaak.ontvangstbevestigingVerstuurd = bevestigd;
        this.setupMenu();
    }

    documentToegevoegd(informatieobject: EnkelvoudigInformatieobject): void {
        this.toegevoegdDocument = informatieobject;
    }
}
