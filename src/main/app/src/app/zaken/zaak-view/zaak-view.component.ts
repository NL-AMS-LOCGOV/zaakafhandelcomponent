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
import {User} from '../../identity/model/user';
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
import {TranslateService} from '@ngx-translate/core';
import {KlantenService} from '../../klanten/klanten.service';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Vertrouwelijkheidaanduiding} from '../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum';
import {ActionsViewComponent} from '../../shared/abstract-view/actions-view-component';
import {Validators} from '@angular/forms';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../shared/confirm-dialog/confirm-dialog.component';
import {Klant} from '../../klanten/model/klanten/klant';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {AddressResult, LocationService} from '../../shared/location/location.service';
import {GeometryType} from '../model/geometryType';
import {SideNavAction} from '../../shared/side-nav/side-nav-action';
import {LocationUtil} from '../../shared/location/location-util';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {UserEventListenerActie} from '../../plan-items/model/user-event-listener-actie-enum';
import {UserEventListenerData} from '../../plan-items/model/user-event-listener-data';
import {detailExpand} from '../../shared/animations/animations';
import {map, tap} from 'rxjs/operators';
import {ExpandableTableData} from '../../shared/dynamic-table/model/expandable-table-data';
import {forkJoin, Observable, of, share, Subscription} from 'rxjs';
import {ZaakOpschorting} from '../model/zaak-opschorting';
import {RadioFormFieldBuilder} from '../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {ZaakVerlengGegevens} from '../model/zaak-verleng-gegevens';
import {ZaakOpschortGegevens} from '../model/zaak-opschort-gegevens';
import {NotificationDialogComponent, NotificationDialogData} from '../../shared/notification-dialog/notification-dialog.component';
import {ZaakKoppelenService} from '../zaak-koppelen/zaak-koppelen.service';
import {GerelateerdeZaak} from '../model/gerelateerde-zaak';
import {ZaakOntkoppelGegevens} from '../model/zaak-ontkoppel-gegevens';
import {ZaakOntkoppelenDialogComponent} from '../zaak-ontkoppelen/zaak-ontkoppelen-dialog.component';
import {PaginaLocatieUtil} from '../../locatie/pagina-locatie.util';
import {KlantGegevens} from '../../klanten/model/klanten/klant-gegevens';
import {ZaakBetrokkene} from '../model/zaak-betrokkene';
import {Adres} from '../../bag/model/adres';
import {BAGObjectGegevens} from '../../bag/model/bagobject-gegevens';
import {BAGObjecttype} from '../../bag/model/bagobjecttype';
import {BAGService} from '../../bag/bag.service';
import {ZaakAfhandelenDialogComponent} from '../zaak-afhandelen-dialog/zaak-afhandelen-dialog.component';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {ZaakStatusmailOptie} from '../model/zaak-statusmail-optie';
import {CustomValidators} from '../../shared/validators/customValidators';
import {MailObject} from '../../mail/model/mailobject';
import {MailService} from '../../mail/mail.service';

@Component({
    templateUrl: './zaak-view.component.html',
    styleUrls: ['./zaak-view.component.less'],
    animations: [detailExpand]
})
export class ZaakViewComponent extends ActionsViewComponent implements OnInit, AfterViewInit, OnDestroy {

    zaak: Zaak;
    zaakLocatie: AddressResult;
    zaakOpschorting: ZaakOpschorting;
    actiefPlanItem: PlanItem;
    menu: MenuItem[];
    action: string;

    taken$: Observable<ExpandableTableData<Taak>[]>;
    takenDataSource: MatTableDataSource<ExpandableTableData<Taak>> = new MatTableDataSource<ExpandableTableData<Taak>>();
    allTakenExpanded: boolean = false;
    toonAfgerondeTaken = false;
    takenFilter: any = {};
    takenColumnsToDisplay: string[] = ['naam', 'status', 'creatiedatumTijd', 'streefdatum', 'groep', 'behandelaar', 'id'];

    toegevoegdDocument: EnkelvoudigInformatieobject;

    historie: MatTableDataSource<HistorieRegel> = new MatTableDataSource<HistorieRegel>();
    historieColumns: string[] = ['datum', 'gebruiker', 'wijziging', 'oudeWaarde', 'nieuweWaarde', 'toelichting'];
    betrokkenen: MatTableDataSource<ZaakBetrokkene> = new MatTableDataSource<ZaakBetrokkene>();
    betrokkenenColumns: string[] = ['roltype', 'betrokkenetype', 'betrokkeneidentificatie', 'roltoelichting', 'rolid'];
    adressen: MatTableDataSource<Adres> = new MatTableDataSource<Adres>();
    adressenColumns: string[] = ['straat', 'huisnummer', 'postcode', 'woonplaats'];
    gerelateerdeZaakColumns: string[] = ['identificatie', 'zaaktypeOmschrijving', 'statustypeOmschrijving', 'startdatum', 'relatieType'];
    notitieType = NotitieType.ZAAK;
    editFormFields: Map<string, any> = new Map<string, any>();
    editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();
    viewInitialized = false;
    toolTipIcon = new TextIcon(Conditionals.always, 'info_outline', 'toolTip_icon', '', 'pointer');
    locatieIcon = new TextIcon(Conditionals.always, 'place', 'locatie_icon', '', 'pointer');

    onderwerp: string = 'Wij hebben uw verzoek in behandeling genomen (zaaknummer: {zaaknr}';
    body: string = 'Beste klant,\n' +
        '\n' +
        'Uw verzoek over {zaaktype naam} met zaaknummer {zaaknr} is in behandeling genomen. Voor meer informatie gaat u naar Mijn loket.\n' +
        '\n' +
        'Met vriendelijke groet,\n' +
        '\n' +
        'Gemeente';

    private zaakListener: WebsocketListener;
    private zaakRollenListener: WebsocketListener;
    private zaakTakenListener: WebsocketListener;
    private ingelogdeMedewerker: User;
    private dialogSubscriptions: Subscription[] = [];

    @ViewChild('actionsSidenav') actionsSidenav: MatSidenav;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;

    @ViewChild('historieSort') historieSort: MatSort;
    @ViewChild('takenSort') takenSort: MatSort;

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
                private locationService: LocationService,
                private zaakKoppelenService: ZaakKoppelenService,
                private bagService: BAGService,
                private mailService: MailService) {
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

        this.takenDataSource.filterPredicate = (data: ExpandableTableData<Taak>, filter: string): boolean => {
            return (!this.toonAfgerondeTaken ? data.data.status !== filter['status'] : true);
        };

        this.toonAfgerondeTaken = SessionStorageUtil.getItem('toonAfgerondeTaken');

    }

    init(zaak: Zaak): void {
        this.zaak = zaak;
        this.utilService.disableActionBar(!zaak.acties.koppelen);
        this.loadHistorie();
        this.loadBetrokkenen();
        this.loadAdressen();
        this.setEditableFormFields();
        this.setupMenu();
        this.loadLocatie();
        this.loadOpschorting();
        this.setPaginaLocatieInformatie(zaak.identificatie);
    }

    private setPaginaLocatieInformatie(zaakIdentificatie: string) {
        PaginaLocatieUtil.actieveZaakViewIdentificatie = zaakIdentificatie;
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
        super.ngAfterViewInit();
        this.takenDataSource.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'groep':
                    return item.data.groep.naam;
                case 'behandelaar' :
                    return item.data.behandelaar.naam;
                default:
                    return item.data[property];
            }
        };
        this.takenDataSource.sort = this.takenSort;

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
        this.historie.sort = this.historieSort;
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
        this.setPaginaLocatieInformatie(null);
        this.utilService.disableActionBar(false);
        this.websocketService.removeListener(this.zaakListener);
        this.websocketService.removeListener(this.zaakRollenListener);
        this.websocketService.removeListener(this.zaakTakenListener);
    }

    private setEditableFormFields(): void {
        this.editFormFields.set('communicatiekanaal',
            new SelectFormFieldBuilder(this.zaak.communicatiekanaal).id('communicatiekanaal').label('communicatiekanaal')
                                                                    .validators(Validators.required)
                                                                    .optionLabel('naam')
                                                                    .options(this.zakenService.listCommunicatiekanalen()).build());

        this.editFormFields.set('behandelaar', new AutocompleteFormFieldBuilder(this.zaak.behandelaar).id('behandelaar').label('behandelaar')
                                                                                                      .optionLabel('naam')
                                                                                                      .options(
                                                                                                          this.identityService.listUsers())
                                                                                                      .maxlength(50)
                                                                                                      .build());
        this.editFormFields.set('groep', new AutocompleteFormFieldBuilder(this.zaak.groep).id('groep').label('groep')
                                                                                          .optionLabel('naam')
                                                                                          .options(this.identityService.listGroups())
                                                                                          .maxlength(50)
                                                                                          .build());
        this.editFormFields.set('omschrijving', new TextareaFormFieldBuilder(this.zaak.omschrijving).id('omschrijving').label('omschrijving')
                                                                                                    .maxlength(80)
                                                                                                    .build());
        this.editFormFields.set('toelichting', new TextareaFormFieldBuilder(this.zaak.toelichting).id('toelichting').label('toelichting')
                                                                                                  .maxlength(1000).build());
        this.editFormFields.set('vertrouwelijkheidaanduiding',
            new SelectFormFieldBuilder(
                {
                    label: this.translate.instant(
                        'vertrouwelijkheidaanduiding.' + this.zaak.vertrouwelijkheidaanduiding),
                    value: 'vertrouwelijkheidaanduiding.' + this.zaak.vertrouwelijkheidaanduiding
                }).id('vertrouwelijkheidaanduiding').label('vertrouwelijkheidaanduiding')
                  .validators(Validators.required)
                  .optionLabel('label')
                  .options(this.utilService.getEnumAsSelectList('vertrouwelijkheidaanduiding',
                      Vertrouwelijkheidaanduiding)).build());

        this.editFormFields.set('startdatum',
            new DateFormFieldBuilder(this.zaak.startdatum).id('startdatum').label('startdatum')
                                                          .validators(Validators.required).build());

        this.editFormFields.set('einddatumGepland',
            new DateFormFieldBuilder(this.zaak.einddatumGepland).id('einddatumGepland').label('einddatumGepland')
                                                                .readonly(!this.zaak.einddatumGepland)
                                                                .validators(this.zaak.einddatumGepland ? Validators.required : Validators.nullValidator)
                                                                .build());
        this.editFormFieldIcons.set('einddatumGepland',
            new TextIcon(Conditionals.isAfterDate(this.zaak.einddatum), 'report_problem', 'warningVerlopen_icon',
                'msg.datum.overschreden', 'warning'));

        this.editFormFields.set('uiterlijkeEinddatumAfdoening',
            new DateFormFieldBuilder(this.zaak.uiterlijkeEinddatumAfdoening).id('uiterlijkeEinddatumAfdoening').label('uiterlijkeEinddatumAfdoening')
                                                                            .validators(Validators.required)
                                                                            .build());
        this.editFormFieldIcons.set('uiterlijkeEinddatumAfdoening',
            new TextIcon(Conditionals.isAfterDate(this.zaak.einddatum), 'report_problem', 'errorVerlopen_icon',
                'msg.datum.overschreden', 'error'));

        this.editFormFields.set('reden', new InputFormFieldBuilder().id('reden').label('reden').validators(Validators.required).maxlength(80).build());
    }

    private createUserEventListenerPlanItemMenuItem(userEventListenerPlanItem: PlanItem): MenuItem {
        if (this.zaak.acties.voortzetten) {
            return new ButtonMenuItem('planitem.' + userEventListenerPlanItem.userEventListenerActie, () =>
                    this.openPlanItemStartenDialog(userEventListenerPlanItem),
                this.getuserEventListenerPlanItemMenuItemIcon(userEventListenerPlanItem.userEventListenerActie));
        } else {
            return null;
        }
    }

    private createHumanTaskPlanItemMenuItem(humanTaskPlanItem: PlanItem): MenuItem {
        return new ButtonMenuItem(humanTaskPlanItem.naam, () => {
            if (!this.actiefPlanItem || this.actiefPlanItem.id !== humanTaskPlanItem.id) {
                this.action = null;
                this.planItemsService.readHumanTaskPlanItem(humanTaskPlanItem.id).subscribe(data => {
                    this.actiefPlanItem = data;
                    this.actionsSidenav.open();
                    this.action = SideNavAction.TAAK_STARTEN;
                });
            } else {
                this.action = SideNavAction.TAAK_STARTEN;
                this.actionsSidenav.open();
            }
        }, 'assignment');
    }

    private getuserEventListenerPlanItemMenuItemIcon(userEventListenerActie: UserEventListenerActie): string {
        switch (userEventListenerActie) {
            case UserEventListenerActie.IntakeAfronden:
                return 'thumbs_up_down';
            case UserEventListenerActie.ZaakAfhandelen:
                return 'thumb_up_alt';
            default:
                return 'fact_check';
        }
    }

    private setupMenu(): void {
        this.menu = [new HeaderMenuItem('zaak')];

        if (this.zaak.acties.versturenOntvangstbevestiging) {
            this.menu.push(new ButtonMenuItem('actie.ontvangstbevestiging.versturen', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.ONTVANGSTBEVESTIGING;
            }, 'mark_email_read'));
        }

        if (this.zaak.acties.versturenEmail) {
            this.menu.push(new ButtonMenuItem('actie.mail.versturen', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.MAIL_VERSTUREN;
            }, 'mail'));
        }

        if (this.zaak.acties.creeerenDocument) {
            this.menu.push(new ButtonMenuItem('actie.document.maken', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.DOCUMENT_MAKEN;
            }, 'note_add'));
        }

        if (this.zaak.acties.toevoegenDocument) {
            this.menu.push(new ButtonMenuItem('actie.document.toevoegen', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.DOCUMENT_TOEVOEGEN;
            }, 'upload_file'));
        }

        if (this.zaak.acties.vastleggenBesluit) {
            this.menu.push(new ButtonMenuItem('actie.besluit.vastleggen', () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.BESLUIT_VASTLEGGEN;
            }, 'gavel'));
        }

        if (this.zaak.isHeropend && this.zaak.acties.voortzetten) {
            this.menu.push(new ButtonMenuItem('actie.zaak.afsluiten', () => this.openZaakAfsluitenDialog(), 'thumb_up_alt'));
        }

        if (this.zaak.acties.heropenen) {
            this.menu.push(new ButtonMenuItem('actie.zaak.heropenen', () => this.openZaakHeropenenDialog(), 'restart_alt'));
        }

        forkJoin({
            userEventListenerPlanItems: this.planItemsService.listUserEventListenerPlanItems(this.zaak.uuid),
            humanTaskPlanItems: this.planItemsService.listHumanTaskPlanItems(this.zaak.uuid)
        }).subscribe(planItems => {
            if (this.zaak.acties.voortzetten && planItems.userEventListenerPlanItems.length > 0) {
                this.menu = this.menu.concat(
                    planItems.userEventListenerPlanItems.map(
                        userEventListenerPlanItem => this.createUserEventListenerPlanItemMenuItem(userEventListenerPlanItem)
                    ).filter(menuItem => menuItem != null));
            }
            if (this.zaak.acties.afbreken) {
                this.menu.push(new ButtonMenuItem('actie.zaak.afbreken', () => this.openZaakAfbrekenDialog(), 'thumb_down_alt'));
            }
            this.createKoppelingenMenuItems();
            if (this.zaak.acties.aanmakenTaak && planItems.humanTaskPlanItems.length > 0) {
                this.menu.push(new HeaderMenuItem('actie.taak.starten'));
                this.menu = this.menu.concat(
                    planItems.humanTaskPlanItems.map(humanTaskPlanItem => this.createHumanTaskPlanItemMenuItem(humanTaskPlanItem)));
            }
        });
    }

    private createKoppelingenMenuItems(): void {
        const zoekInitiator: boolean = (this.zaak.acties.toevoegenInitiatorPersoon || this.zaak.acties.toevoegenInitiatorBedrijf) &&
            (this.zaak.initiatorIdentificatie == null || this.zaak.acties.verwijderenInitiator);
        const zoekBetrokkene: boolean = this.zaak.acties.toevoegenBetrokkenePersoon || this.zaak.acties.toevoegenBetrokkeneBedrijf;
        const zoekBAG: boolean = this.zaak.acties.toevoegenBAGObject;
        const zaakToClipboard: boolean = this.zaak.acties.koppelen;
        if (zoekInitiator || zoekBetrokkene || zoekBAG || zaakToClipboard) {
            this.menu.push(new HeaderMenuItem('koppelingen'));
            if (zoekInitiator) {
                this.menu.push(new ButtonMenuItem('actie.initiator.toevoegen', () => {
                    this.actionsSidenav.open();
                    this.action = SideNavAction.ZOEK_INITIATOR;
                }, 'person_add_alt_1'));
            }
            if (zoekBetrokkene) {
                this.menu.push(new ButtonMenuItem('actie.betrokkene.toevoegen', () => {
                    this.actionsSidenav.open();
                    this.action = SideNavAction.ZOEK_BETROKKENE;
                }, 'group_add'));
            }
            if (zoekBAG) {
                this.menu.push(new ButtonMenuItem('actie.bagobject.toevoegen', () => {
                    this.actionsSidenav.open();
                    this.action = SideNavAction.ZOEK_BAG_ADRES;
                }, 'add_home_work'));
            }
            if (zaakToClipboard) {
                this.menu.push(new ButtonMenuItem('actie.zaak.koppelen', () => {
                    this.zaakKoppelenService.addTeKoppelenZaak(this.zaak);
                }, 'account_tree'));
            }
        }
    }

    openPlanItemStartenDialog(planItem: PlanItem): void {
        this.actionsSidenav.close();
        this.websocketService.doubleSuspendListener(this.zaakListener);
        const userEventListenerDialog = this.createUserEventListenerDialog(planItem);
        this.dialog.open(userEventListenerDialog.dialogComponent, {
            data: userEventListenerDialog.dialogData
        }).afterClosed().subscribe(result => {
            for (const subscription of this.dialogSubscriptions) {
                subscription.unsubscribe();
            }
            this.dialogSubscriptions.length = 0;
            if (result) {
                if (result === 'openBesluitVastleggen') {
                    this.actionsSidenav.open();
                    this.action = SideNavAction.BESLUIT_VASTLEGGEN;
                } else {
                    this.utilService.openSnackbar('msg.planitem.uitgevoerd.' + planItem.userEventListenerActie);
                    this.updateZaak();
                }
            }
        });
    }

    createUserEventListenerDialog(planItem: PlanItem): { dialogComponent: any, dialogData: any } {
        switch (planItem.userEventListenerActie) {
            case UserEventListenerActie.IntakeAfronden:
                return this.createUserEventListenerIntakeAfrondenDialog(planItem);
            case UserEventListenerActie.ZaakAfhandelen:
                return this.createUserEventListenerZaakAfhandelenDialog(planItem);
            default:
                throw new Error(`Niet bestaande UserEventListenerActie: ${planItem.userEventListenerActie}`);
        }
    }

    createUserEventListenerIntakeAfrondenDialog(planItem: PlanItem): { dialogComponent: any, dialogData: any } {
        const radio = new RadioFormFieldBuilder().id('ontvankelijk')
                                                 .label('zaakOntvankelijk')
                                                 .optionLabel('key')
                                                 .options(of([
                                                     {value: true, key: 'actie.ja'},
                                                     {value: false, key: 'actie.nee'}]))
                                                 .validators(Validators.required)
                                                 .build();
        const reden = new TextareaFormFieldBuilder().id('reden')
                                                    .label('redenNietOntvankelijk')
                                                    .validators(Validators.required)
                                                    .maxlength(100)
                                                    .build();
        const sendMail = new CheckboxFormFieldBuilder().id('sendMail')
                                                       .label('sendMail')
                                                       .build();
        const ontvangerFormField = new InputFormFieldBuilder().id('ontvanger')
                                                              .label('ontvanger')
                                                              .validators(Validators.required, CustomValidators.email)
                                                              .maxlength(200)
                                                              .build();

        const dialogData: DialogData = new DialogData(
            [radio],
            (results: any[]) => this.doUserEventListenerIntakeAfronden(planItem.id, results['ontvankelijk'].value, results['reden'],
                results['sendMail'] ? results['ontvanger'] : null),
            null,
            planItem.toelichting);
        dialogData.confirmButtonActionKey = 'planitem.' + planItem.userEventListenerActie;

        this.dialogSubscriptions.push(radio.formControl.valueChanges.subscribe(value => {
            if (value) {
                if (value.value) {
                    dialogData.formFields = dialogData.formFields.filter(ff => ff !== reden);
                } else if (!dialogData.formFields.find(ff => ff === reden)) {
                    dialogData.formFields = [radio, reden, ...dialogData.formFields.slice(1)];
                    // Nodig om te zorgen dat `reden` direct verplicht is
                    dialogData.formFields.forEach(ff => ff.formControl.updateValueAndValidity());
                }
            }
        }));
        this.dialogSubscriptions.push(sendMail.formControl.valueChanges.subscribe(value => {
            if (!value) {
                dialogData.formFields = dialogData.formFields.filter(ff => ff !== ontvangerFormField);
            } else if (!dialogData.formFields.find(ff => ff === ontvangerFormField)) {
                dialogData.formFields = [...dialogData.formFields, ontvangerFormField];
            }
        }));
        this.dialogSubscriptions.push(
            this.zaakafhandelParametersService.readZaakafhandelparameters(this.zaak.zaaktype.uuid).subscribe(zaakafhandelParameters => {
                if (zaakafhandelParameters.intakeMail !== ZaakStatusmailOptie.NIET_BESCHIKBAAR) {
                    sendMail.formControl.enable();
                    dialogData.formFields = [...dialogData.formFields, sendMail];
                }
                sendMail.formControl.setValue(zaakafhandelParameters.intakeMail === ZaakStatusmailOptie.BESCHIKBAAR_AAN);
            }));

        return {
            dialogComponent: DialogComponent,
            dialogData: dialogData
        };
    }

    private doUserEventListenerIntakeAfronden(planItemId: string, ontvankelijk: boolean, toelichting: string, ontvanger: string | null): Observable<void> {
        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.IntakeAfronden, planItemId, this.zaak.uuid);
        userEventListenerData.zaakOntvankelijk = ontvankelijk;
        userEventListenerData.resultaatToelichting = toelichting;

        this.onderwerp = this.onderwerp.replace('{zaaknr}', this.zaak.identificatie);
        this.body = this.body.replace('{zaaktype naam}', this.zaak.zaaktype.identificatie)
                        .replace('{zaaknr}', this.zaak.identificatie);
        if (ontvanger !== null) {
            const mailObject: MailObject = new MailObject();
            mailObject.createDocumentFromMail = true;
            mailObject.onderwerp = this.onderwerp;
            mailObject.body = this.body;
            mailObject.ontvanger = ontvanger;
            this.mailService.sendMail(this.zaak.uuid, mailObject).subscribe(() => {});
        }

        return this.planItemsService.doUserEventListenerPlanItem(userEventListenerData);
    }

    createUserEventListenerZaakAfhandelenDialog(planItem: PlanItem): { dialogComponent: any, dialogData: any } {
        return {
            dialogComponent: ZaakAfhandelenDialogComponent,
            dialogData: {zaak: this.zaak, planItem: planItem}
        };
    }

    private openZaakAfbrekenDialog(): void {
        this.actionsSidenav.close();
        const dialogData = new DialogData([
                new SelectFormFieldBuilder().id('reden')
                                            .label('actie.zaak.afbreken.reden')
                                            .optionLabel('naam')
                                            .options(this.zaakafhandelParametersService.listZaakbeeindigRedenenForZaaktype(this.zaak.zaaktype.uuid))
                                            .validators(Validators.required)
                                            .build()],
            (results: any[]) => this.zakenService.afbreken(this.zaak.uuid, results['reden']).pipe(
                tap(() => this.websocketService.suspendListener(this.zaakListener))
            ));

        dialogData.confirmButtonActionKey = 'actie.zaak.afbreken';

        this.dialog.open(DialogComponent, {data: dialogData}).afterClosed().subscribe(result => {
            if (result) {
                this.updateZaak();
                this.loadTaken();
                this.utilService.openSnackbar('msg.zaak.afgebroken');
            }
        });
    }

    private openZaakHeropenenDialog(): void {
        const dialogData = new DialogData([
                new InputFormFieldBuilder().id('reden').label('actie.zaak.heropenen.reden').validators(Validators.required).maxlength(100).build()],
            (results: any[]) => this.zakenService.heropenen(this.zaak.uuid, results['reden']).pipe(
                tap(() => this.websocketService.suspendListener(this.zaakListener))
            ));

        dialogData.confirmButtonActionKey = 'actie.zaak.heropenen';

        this.dialog.open(DialogComponent, {data: dialogData}).afterClosed().subscribe(result => {
            if (result) {
                this.updateZaak();
                this.loadTaken();
                this.utilService.openSnackbar('msg.zaak.heropend');
            }
        });
    }

    private openZaakAfsluitenDialog(): void {
        this.actionsSidenav.close();
        const dialogData = new DialogData([
                new SelectFormFieldBuilder().id('resultaattype')
                                            .label('resultaat')
                                            .optionLabel('naam')
                                            .options(this.zakenService.listResultaattypes(this.zaak.zaaktype.uuid))
                                            .validators(Validators.required)
                                            .build(),
                new InputFormFieldBuilder().id('toelichting')
                                           .label('toelichting')
                                           .maxlength(80)
                                           .build()],
            (results: any[]) => this.zakenService.afsluiten(this.zaak.uuid, results['toelichting'], results['resultaattype'].id).pipe(
                tap(() => this.websocketService.suspendListener(this.zaakListener))
            ));

        dialogData.confirmButtonActionKey = 'actie.zaak.afsluiten';

        this.dialog.open(DialogComponent, {data: dialogData}).afterClosed().subscribe(result => {
            if (result) {
                this.updateZaak();
                this.loadTaken();
                this.utilService.openSnackbar('msg.zaak.afgesloten');
            }
        });
    }

    editDatumGroep(event: any): void {
        const zaak: Zaak = new Zaak();
        zaak.startdatum = event.startdatum;
        zaak.einddatumGepland = event.einddatumGepland;
        zaak.uiterlijkeEinddatumAfdoening = event.uiterlijkeEinddatumAfdoening;
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.updateZaak(this.zaak.uuid, zaak, event.reden).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    editOpschorting(event: any): void {
        const zaakOpschortGegevens = new ZaakOpschortGegevens();
        zaakOpschortGegevens.indicatieOpschorting = !this.zaak.isOpgeschort;
        zaakOpschortGegevens.einddatumGepland = event.einddatumGepland;
        zaakOpschortGegevens.uiterlijkeEinddatumAfdoening = event.uiterlijkeEinddatumAfdoening;
        zaakOpschortGegevens.redenOpschorting = event.reden;
        zaakOpschortGegevens.duurDagen = event.duurDagen;
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.opschortenZaak(this.zaak.uuid, zaakOpschortGegevens).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    editVerlenging(event: any): void {
        const zaakVerlengGegevens = new ZaakVerlengGegevens();
        zaakVerlengGegevens.einddatumGepland = event.einddatumGepland;
        zaakVerlengGegevens.uiterlijkeEinddatumAfdoening = event.uiterlijkeEinddatumAfdoening;
        zaakVerlengGegevens.redenVerlenging = event.reden;
        zaakVerlengGegevens.duurDagen = event.duurDagen;
        zaakVerlengGegevens.takenVerlengen = event.takenVerlengen;
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.verlengenZaak(this.zaak.uuid, zaakVerlengGegevens).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    private loadOpschorting(): void {
        if (this.zaak.isOpgeschort) {
            this.zakenService.readOpschortingZaak(this.zaak.uuid).subscribe(objectData => {
                this.zaakOpschorting = objectData;
            });
        }
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
        if (event.behandelaar && event.behandelaar.id === this.ingelogdeMedewerker.id) {
            this.assignZaakToMe(event);
        } else if (event.behandelaar) {
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
        this.zakenService.vrijgeven([this.zaak.uuid], reden).subscribe(() => {
            this.init(this.zaak);
            this.utilService.openSnackbar('msg.zaak.vrijgegeven');
        });
    }

    editZaakMetReden(event: any, field: string): void {
        const zaak: Zaak = new Zaak();
        zaak[field] = event[field].value ? event[field].value : event[field];
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.updateZaak(this.zaak.uuid, zaak, event.reden).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    editZaak(value: string, field: string): void {
        const zaak: Zaak = new Zaak();
        zaak[field] = value;
        this.websocketService.suspendListener(this.zaakListener);
        this.zakenService.updateZaak(this.zaak.uuid, zaak).subscribe(updatedZaak => {
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

    private loadBetrokkenen(): void {
        this.zakenService.listBetrokkenenVoorZaak(this.zaak.uuid).subscribe(betrokkenen => {
            this.betrokkenen.data = betrokkenen;
        });
    }

    private loadAdressen(): void {
        this.bagService.listAdressenVoorZaak(this.zaak.uuid).subscribe(adressen => {
            this.adressen.data = adressen;
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

    private loadTaken(event?: ScreenEvent): void {
        if (event) {
            console.log('callback loadTaken: ' + event.key);
        }

        this.taken$ = this.takenService.listTakenVoorZaak(this.zaak.uuid)
                          .pipe(
                              share(),
                              map(values => values.map(value => new ExpandableTableData(value)))
                          );
        this.taken$.subscribe(taken => {
            taken = taken.sort((a, b) => a.data.streefdatum?.localeCompare(b.data.streefdatum) ||
                a.data.creatiedatumTijd?.localeCompare(b.data.creatiedatumTijd));
            this.takenDataSource.data = taken;
            this.filterTakenOpStatus();
        });
    }

    expandTaken(expand: boolean): void {
        this.takenDataSource.data.forEach(value => value.expanded = expand);
        this.checkAllTakenExpanded();
    }

    expandTaak(taak: ExpandableTableData<Taak>): void {
        taak.expanded = !taak.expanded;
        this.checkAllTakenExpanded();
    }

    checkAllTakenExpanded(): void {
        const filter: ExpandableTableData<Taak>[] = this.toonAfgerondeTaken ? this.takenDataSource.data.filter(value => !value.expanded) :
            this.takenDataSource.data.filter(value => value.data.status !== 'AFGEROND' && !value.expanded);

        this.allTakenExpanded = filter.length === 0;
    }

    showAssignTaakToMe(taak: Taak): boolean {
        return taak.acties.wijzigenToekenning && this.ingelogdeMedewerker.id !== taak.behandelaar?.id;
    }

    private assignZaakToMe(event: any): void {
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
        this.zakenService.updateZaakGeometrie(this.zaak.uuid, zaak).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    initiatorGeselecteerd(initiator: Klant): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.actionsSidenav.close();
        this.zakenService.updateInitiator(this.zaak, initiator)
            .subscribe(zaak => {
                this.zaak = zaak;
                this.utilService.openSnackbar('msg.initiator.toegevoegd', {naam: zaak.initiatorIdentificatie});
                this.setupMenu();
                this.loadHistorie();
            });
    }

    deleteInitiator(): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('msg.initiator.ontkoppelen.bevestigen'),
                this.zakenService.deleteInitiator(this.zaak)
            )
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('msg.initiator.ontkoppelen.uitgevoerd');
                this.zakenService.readZaak(this.zaak.uuid).subscribe(zaak => {
                    this.zaak = zaak;
                    this.setupMenu();
                    this.loadHistorie();
                });
            }
        });
    }

    betrokkeneGeselecteerd(betrokkene: KlantGegevens): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        this.actionsSidenav.close();
        this.zakenService.createBetrokkene(this.zaak, betrokkene.klant, betrokkene.betrokkeneRoltype, betrokkene.betrokkeneToelichting)
            .subscribe(zaak => {
                this.zaak = zaak;
                this.utilService.openSnackbar('msg.betrokkene.toegevoegd', {roltype: betrokkene.betrokkeneRoltype.naam});
                this.loadHistorie();
                this.loadBetrokkenen();
            });
    }

    deleteBetrokkene(betrokkene: ZaakBetrokkene): void {
        this.websocketService.suspendListener(this.zaakRollenListener);
        const betrokkeneIdentificatie: string = betrokkene.roltype + ' ' + betrokkene.identificatie;
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('msg.betrokkene.ontkoppelen.bevestigen', {betrokkene: betrokkeneIdentificatie}),
                this.zakenService.deleteBetrokkene(betrokkene.rolid)
            )
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('msg.betrokkene.ontkoppelen.uitgevoerd', {betrokkene: betrokkeneIdentificatie});
                this.zakenService.readZaak(this.zaak.uuid).subscribe(zaak => {
                    this.zaak = zaak;
                    this.loadHistorie();
                    this.loadBetrokkenen();
                });
            }
        });
    }

    adresGeselecteerd(adres: Adres): void {
        this.websocketService.suspendListener(this.zaakListener);
        this.actionsSidenav.close();
        this.bagService.createBAGObject(new BAGObjectGegevens(this.zaak.uuid, adres.url, BAGObjecttype.ADRES)).subscribe(() => {
            this.utilService.openSnackbar('msg.bagobject.toegevoegd');
            this.loadHistorie();
            this.loadAdressen();
        });
    }

    assignTaakToMe(taak: Taak, $event) {
        $event.stopPropagation();

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

    taakGestart(): void {
        this.actiefPlanItem = null;
        this.actionsSidenav.close();
        this.updateZaak();
    }

    mailVerstuurd(): void {
        this.action = null;
        this.actionsSidenav.close();
        this.updateZaak();
    }

    ontvangstBevestigd(): void {
        this.action = null;
        this.actionsSidenav.close();
        this.updateZaak();
    }

    documentToegevoegd(informatieobject: EnkelvoudigInformatieobject): void {
        this.toegevoegdDocument = informatieobject;
        this.updateZaak();
    }

    documentAanmakenStarten(redirectUrl: string): void {
        this.action = null;
        this.actionsSidenav.close();
        window.open(redirectUrl);
    }

    documentAanmakenNietMogelijk(melding: string): void {
        this.action = null;
        this.actionsSidenav.close();
        this.dialog.open(NotificationDialogComponent, {data: new NotificationDialogData(melding)});
    }

    startZaakOntkoppelenDialog(gerelateerdeZaak: GerelateerdeZaak): void {
        const zaakOntkoppelGegevens: ZaakOntkoppelGegevens = new ZaakOntkoppelGegevens();
        zaakOntkoppelGegevens.teOntkoppelenZaakUUID = this.zaak.uuid;
        zaakOntkoppelGegevens.ontkoppelenVanZaakIdentificatie = gerelateerdeZaak.identificatie;
        zaakOntkoppelGegevens.zaakRelatietype = gerelateerdeZaak.relatieType;

        this.dialog.open(ZaakOntkoppelenDialogComponent, {
            data: zaakOntkoppelGegevens
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('msg.zaak.ontkoppelen.uitgevoerd');
            }
        });
    }

    besluitVastgelegd(besluitVastgelegd: boolean): void {
        if (besluitVastgelegd) {
            this.updateZaak();
        }
        this.action = null;
        this.actionsSidenav.close();
    }

    besluitWijzigen(): void {
        this.action = SideNavAction.BESLUIT_WIJZIGEN;
        this.actionsSidenav.open();
    }
}
