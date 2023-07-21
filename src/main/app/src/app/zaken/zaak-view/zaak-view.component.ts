/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Taak } from "../../taken/model/taak";
import { UtilService } from "../../core/service/util.service";
import { MenuItem } from "../../shared/side-nav/menu-item/menu-item";
import { InformatieObjectenService } from "../../informatie-objecten/informatie-objecten.service";
import { TakenService } from "../../taken/taken.service";
import { Zaak } from "../model/zaak";
import { PlanItemsService } from "../../plan-items/plan-items.service";
import { PlanItem } from "../../plan-items/model/plan-item";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { HeaderMenuItem } from "../../shared/side-nav/menu-item/header-menu-item";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { ZakenService } from "../zaken.service";
import { WebsocketService } from "../../core/websocket/websocket.service";
import { Opcode } from "../../core/websocket/model/opcode";
import { ObjectType } from "../../core/websocket/model/object-type";
import { NotitieType } from "../../notities/model/notitietype.enum";
import { WebsocketListener } from "../../core/websocket/model/websocket-listener";
import { HistorieRegel } from "../../shared/historie/model/historie-regel";
import { TextareaFormFieldBuilder } from "../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { LoggedInUser } from "../../identity/model/logged-in-user";
import { IdentityService } from "../../identity/identity.service";
import { DateFormFieldBuilder } from "../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { TextIcon } from "../../shared/edit/text-icon";
import { Conditionals } from "../../shared/edit/conditional-fn";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { MatDialog } from "@angular/material/dialog";
import { ButtonMenuItem } from "../../shared/side-nav/menu-item/button-menu-item";
import { DialogComponent } from "../../shared/dialog/dialog.component";
import { DialogData } from "../../shared/dialog/dialog-data";
import { TranslateService } from "@ngx-translate/core";
import { KlantenService } from "../../klanten/klanten.service";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { Vertrouwelijkheidaanduiding } from "../../informatie-objecten/model/vertrouwelijkheidaanduiding.enum";
import { ActionsViewComponent } from "../../shared/abstract-view/actions-view-component";
import { Validators } from "@angular/forms";
import { ZaakafhandelParametersService } from "../../admin/zaakafhandel-parameters.service";
import { Klant } from "../../klanten/model/klanten/klant";
import { SessionStorageUtil } from "../../shared/storage/session-storage.util";
import { LocationService } from "../../shared/location/location.service";
import { SideNavAction } from "../../shared/side-nav/side-nav-action";
import { UserEventListenerActie } from "../../plan-items/model/user-event-listener-actie-enum";
import { detailExpand } from "../../shared/animations/animations";
import { map, tap } from "rxjs/operators";
import { ExpandableTableData } from "../../shared/dynamic-table/model/expandable-table-data";
import { forkJoin } from "rxjs";
import { ZaakOpschorting } from "../model/zaak-opschorting";
import { ZaakVerlengGegevens } from "../model/zaak-verleng-gegevens";
import { ZaakOpschortGegevens } from "../model/zaak-opschort-gegevens";
import {
  NotificationDialogComponent,
  NotificationDialogData,
} from "../../shared/notification-dialog/notification-dialog.component";
import { ZaakKoppelenService } from "../zaak-koppelen/zaak-koppelen.service";
import { GerelateerdeZaak } from "../model/gerelateerde-zaak";
import { ZaakOntkoppelGegevens } from "../model/zaak-ontkoppel-gegevens";
import { ZaakOntkoppelenDialogComponent } from "../zaak-ontkoppelen/zaak-ontkoppelen-dialog.component";
import { ViewResourceUtil } from "../../locatie/view-resource.util";
import { KlantGegevens } from "../../klanten/model/klanten/klant-gegevens";
import { ZaakBetrokkene } from "../model/zaak-betrokkene";
import { BAGObjectGegevens } from "../../bag/model/bagobject-gegevens";
import { BAGService } from "../../bag/bag.service";
import { ZaakAfhandelenDialogComponent } from "../zaak-afhandelen-dialog/zaak-afhandelen-dialog.component";
import { MedewerkerGroepFieldBuilder } from "../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-field-builder";
import { IntakeAfrondenDialogComponent } from "../intake-afronden-dialog/intake-afronden-dialog.component";
import { TaakStatus } from "../../taken/model/taak-status.enum";
import { IndicatiesLayout } from "../../shared/indicaties/indicaties.component";
import { Besluit } from "../model/besluit";
import { DatumPipe } from "../../shared/pipes/datum.pipe";
import { DocumentCreatieGegevens } from "../../informatie-objecten/model/document-creatie-gegevens";
import { BAGObject } from "../../bag/model/bagobject";
import { BesluitIntrekkenGegevens } from "../model/besluit-intrekken-gegevens";
import { GeometryGegevens } from "../model/geometry-gegevens";
import { OrderUtil } from "../../shared/order/order-util";
import { ReadonlyFormFieldBuilder } from "../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";

@Component({
  templateUrl: "./zaak-view.component.html",
  styleUrls: ["./zaak-view.component.less"],
  animations: [detailExpand],
})
export class ZaakViewComponent
  extends ActionsViewComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  readonly indicatiesLayout = IndicatiesLayout;
  zaak: Zaak;
  zaakOpschorting: ZaakOpschorting;
  actiefPlanItem: PlanItem;
  menu: MenuItem[];
  readonly sideNavAction = SideNavAction;
  action: SideNavAction;
  teWijzigenBesluit: Besluit;

  takenDataSource: MatTableDataSource<ExpandableTableData<Taak>> =
    new MatTableDataSource<ExpandableTableData<Taak>>();
  allTakenExpanded = false;
  toonAfgerondeTaken = false;
  takenFilter: any = {};
  takenLoading = false;
  takenColumnsToDisplay: string[] = [
    "naam",
    "status",
    "creatiedatumTijd",
    "fataledatum",
    "groep",
    "behandelaar",
    "id",
  ];

  historie: MatTableDataSource<HistorieRegel> =
    new MatTableDataSource<HistorieRegel>();
  historieColumns: string[] = [
    "datum",
    "gebruiker",
    "wijziging",
    "oudeWaarde",
    "nieuweWaarde",
    "toelichting",
  ];
  betrokkenen: MatTableDataSource<ZaakBetrokkene> =
    new MatTableDataSource<ZaakBetrokkene>();
  betrokkenenColumns: string[] = [
    "roltype",
    "betrokkenegegevens",
    "betrokkeneidentificatie",
    "roltoelichting",
    "actions",
  ];
  bagObjectenDataSource: MatTableDataSource<BAGObjectGegevens> =
    new MatTableDataSource<BAGObjectGegevens>();
  gekoppeldeBagObjecten: BAGObject[];
  bagObjectenColumns: string[] = [
    "identificatie",
    "type",
    "omschrijving",
    "actions",
  ];
  gerelateerdeZaakColumns: string[] = [
    "identificatie",
    "zaaktypeOmschrijving",
    "statustypeOmschrijving",
    "startdatum",
    "relatieType",
  ];
  notitieType = NotitieType.ZAAK;
  editFormFields: Map<string, any> = new Map<string, any>();
  editFormFieldIcons: Map<string, TextIcon> = new Map<string, TextIcon>();
  viewInitialized = false;
  toolTipIcon = new TextIcon(
    Conditionals.always,
    "info",
    "toolTip_icon",
    "",
    "pointer",
    true,
  );

  private zaakListener: WebsocketListener;
  private zaakRollenListener: WebsocketListener;
  private zaakBesluitenListener: WebsocketListener;
  private zaakTakenListener: WebsocketListener;
  private ingelogdeMedewerker: LoggedInUser;
  private datumPipe = new DatumPipe("nl");

  @ViewChild("actionsSidenav") actionsSidenav: MatSidenav;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;

  @ViewChild("historieSort") historieSort: MatSort;
  @ViewChild("takenSort") takenSort: MatSort;

  constructor(
    private informatieObjectenService: InformatieObjectenService,
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
  ) {
    super();
  }

  ngOnInit(): void {
    this.subscriptions$.push(
      this.route.data.subscribe((data) => {
        this.init(data["zaak"]);

        this.zaakListener = this.websocketService.addListenerWithSnackbar(
          Opcode.ANY,
          ObjectType.ZAAK,
          this.zaak.uuid,
          () => this.updateZaak(),
        );

        this.zaakRollenListener = this.websocketService.addListenerWithSnackbar(
          Opcode.UPDATED,
          ObjectType.ZAAK_ROLLEN,
          this.zaak.uuid,
          () => this.updateZaak(),
        );

        this.zaakBesluitenListener =
          this.websocketService.addListenerWithSnackbar(
            Opcode.UPDATED,
            ObjectType.ZAAK_BESLUITEN,
            this.zaak.uuid,
            () => this.loadBesluiten(),
          );

        this.zaakTakenListener = this.websocketService.addListener(
          Opcode.UPDATED,
          ObjectType.ZAAK_TAKEN,
          this.zaak.uuid,
          () => this.loadTaken(),
        );

        this.utilService.setTitle("title.zaak", {
          zaak: this.zaak.identificatie,
        });

        this.getIngelogdeMedewerker();
        this.loadTaken();
      }),
    );

    this.takenDataSource.filterPredicate = (
      data: ExpandableTableData<Taak>,
      filter: string,
    ): boolean => {
      return !this.toonAfgerondeTaken
        ? data.data.status !== filter["status"]
        : true;
    };

    this.toonAfgerondeTaken = SessionStorageUtil.getItem("toonAfgerondeTaken");
  }

  init(zaak: Zaak): void {
    this.zaak = zaak;
    this.utilService.disableActionBar(!zaak.rechten.wijzigen);
    this.loadHistorie();
    this.loadBetrokkenen();
    this.loadBagObjecten();
    this.setEditableFormFields();
    this.setupMenu();
    this.loadOpschorting();
    ViewResourceUtil.actieveZaak = zaak;
  }

  private getIngelogdeMedewerker() {
    this.identityService.readLoggedInUser().subscribe((ingelogdeMedewerker) => {
      this.ingelogdeMedewerker = ingelogdeMedewerker;
    });
  }

  ngAfterViewInit() {
    this.viewInitialized = true;
    super.ngAfterViewInit();

    this.subscriptions$.push(
      this.actionsSidenav.openedChange.subscribe((opened) => {
        if (!opened && this.action === SideNavAction.ZOEK_LOCATIE) {
          this.action = null;
        }
      }),
    );

    this.takenDataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case "groep":
          return item.data.groep.naam;
        case "behandelaar":
          return item.data.behandelaar.naam;
        default:
          return item.data[property];
      }
    };
    this.takenDataSource.sort = this.takenSort;

    this.historie.sortingDataAccessor = (item, property) => {
      switch (property) {
        case "datum":
          return item.datumTijd;
        case "gebruiker":
          return item.door;
        default:
          return item[property];
      }
    };
    this.historie.sort = this.historieSort;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    ViewResourceUtil.actieveZaak = null;
    this.utilService.disableActionBar(false);
    this.websocketService.removeListener(this.zaakListener);
    this.websocketService.removeListener(this.zaakBesluitenListener);
    this.websocketService.removeListener(this.zaakRollenListener);
    this.websocketService.removeListener(this.zaakTakenListener);
  }

  private setEditableFormFields(): void {
    this.editFormFields.set(
      "communicatiekanaal",
      new SelectFormFieldBuilder(this.zaak.communicatiekanaal)
        .id("communicatiekanaal")
        .label("communicatiekanaal")
        .validators(Validators.required)
        .optionLabel("naam")
        .options(this.zakenService.listCommunicatiekanalen())
        .build(),
    );

    this.editFormFields.set(
      "medewerker-groep",
      new MedewerkerGroepFieldBuilder(this.zaak.groep, this.zaak.behandelaar)
        .id("medewerker-groep")
        .groepLabel("groep.-kies-")
        .groepRequired()
        .medewerkerLabel("behandelaar.-kies-")
        .build(),
    );
    this.editFormFields.set(
      "omschrijving",
      new TextareaFormFieldBuilder(this.zaak.omschrijving)
        .id("omschrijving")
        .label("omschrijving")
        .maxlength(80)
        .build(),
    );
    this.editFormFields.set(
      "toelichting",
      new TextareaFormFieldBuilder(this.zaak.toelichting)
        .id("toelichting")
        .label("toelichting")
        .maxlength(1000)
        .build(),
    );
    this.editFormFields.set(
      "vertrouwelijkheidaanduiding",
      new SelectFormFieldBuilder({
        label: this.translate.instant(
          "vertrouwelijkheidaanduiding." +
            this.zaak.vertrouwelijkheidaanduiding,
        ),
        value:
          "vertrouwelijkheidaanduiding." +
          this.zaak.vertrouwelijkheidaanduiding,
      })
        .id("vertrouwelijkheidaanduiding")
        .label("vertrouwelijkheidaanduiding")
        .validators(Validators.required)
        .optionLabel("label")
        .options(
          this.utilService.getEnumAsSelectList(
            "vertrouwelijkheidaanduiding",
            Vertrouwelijkheidaanduiding,
          ),
        )
        .optionsOrder(OrderUtil.orderAsIs())
        .build(),
    );

    this.editFormFields.set(
      "startdatum",
      new DateFormFieldBuilder(this.zaak.startdatum)
        .id("startdatum")
        .label("startdatum")
        .validators(Validators.required)
        .build(),
    );

    this.editFormFields.set(
      "einddatumGepland",
      new DateFormFieldBuilder(this.zaak.einddatumGepland)
        .id("einddatumGepland")
        .label("einddatumGepland")
        .readonly(!this.zaak.einddatumGepland)
        .validators(
          this.zaak.einddatumGepland
            ? Validators.required
            : Validators.nullValidator,
        )
        .build(),
    );
    this.editFormFieldIcons.set(
      "einddatumGepland",
      new TextIcon(
        Conditionals.isAfterDate(this.zaak.einddatum),
        "report_problem",
        "warningVerlopen_icon",
        "msg.datum.overschreden",
        "warning",
      ),
    );

    this.editFormFields.set(
      "uiterlijkeEinddatumAfdoening",
      new DateFormFieldBuilder(this.zaak.uiterlijkeEinddatumAfdoening)
        .id("uiterlijkeEinddatumAfdoening")
        .label("uiterlijkeEinddatumAfdoening")
        .validators(Validators.required)
        .build(),
    );
    this.editFormFieldIcons.set(
      "uiterlijkeEinddatumAfdoening",
      new TextIcon(
        Conditionals.isAfterDate(this.zaak.einddatum),
        "report_problem",
        "errorVerlopen_icon",
        "msg.datum.overschreden",
        "error",
      ),
    );

    this.editFormFields.set(
      "reden",
      new InputFormFieldBuilder()
        .id("reden")
        .label("reden")
        .validators(Validators.required)
        .maxlength(80)
        .build(),
    );
    this.editFormFields.set(
      "redenNietVerplicht",
      new InputFormFieldBuilder()
        .id("reden")
        .label("reden")
        .maxlength(80)
        .build(),
    );
  }

  private createUserEventListenerPlanItemMenuItem(
    userEventListenerPlanItem: PlanItem,
  ): MenuItem {
    return new ButtonMenuItem(
      "planitem." + userEventListenerPlanItem.userEventListenerActie,
      () => this.openPlanItemStartenDialog(userEventListenerPlanItem),
      this.getuserEventListenerPlanItemMenuItemIcon(
        userEventListenerPlanItem.userEventListenerActie,
      ),
    );
  }

  private createHumanTaskPlanItemMenuItem(
    humanTaskPlanItem: PlanItem,
  ): MenuItem {
    return new ButtonMenuItem(
      humanTaskPlanItem.naam,
      () => {
        if (
          !this.actiefPlanItem ||
          this.actiefPlanItem.id !== humanTaskPlanItem.id
        ) {
          this.action = null;
          this.planItemsService
            .readHumanTaskPlanItem(humanTaskPlanItem.id)
            .subscribe((planItem) => {
              this.actiefPlanItem = planItem;
              this.action = SideNavAction.TAAK_STARTEN;
              this.actionsSidenav.open();
            });
        } else {
          this.action = SideNavAction.TAAK_STARTEN;
          this.actionsSidenav.open();
        }
      },
      "assignment",
    );
  }

  private createProcessTaskPlanItemMenuItem(
    processTaskPlanItem: PlanItem,
  ): MenuItem {
    return new ButtonMenuItem(
      processTaskPlanItem.naam,
      () => {
        if (
          !this.actiefPlanItem ||
          this.actiefPlanItem.id !== processTaskPlanItem.id
        ) {
          this.action = null;
          this.planItemsService
            .readProcessTaskPlanItem(processTaskPlanItem.id)
            .subscribe((planItem) => {
              this.actiefPlanItem = planItem;
              this.action = SideNavAction.PROCESS_STARTEN;
              this.actionsSidenav.open();
            });
        } else {
          this.action = SideNavAction.PROCESS_STARTEN;
          this.actionsSidenav.open();
        }
      },
      "receipt_long",
    );
  }

  private getuserEventListenerPlanItemMenuItemIcon(
    userEventListenerActie: UserEventListenerActie,
  ): string {
    switch (userEventListenerActie) {
      case UserEventListenerActie.IntakeAfronden:
        return "thumbs_up_down";
      case UserEventListenerActie.ZaakAfhandelen:
        return "thumb_up_alt";
      default:
        return "fact_check";
    }
  }

  private setupMenu(): void {
    this.menu = [new HeaderMenuItem("zaak")];

    if (this.zaak.rechten.behandelen && !this.zaak.isProcesGestuurd) {
      if (!this.zaak.isOntvangstbevestigingVerstuurd) {
        this.menu.push(
          new ButtonMenuItem(
            "actie.ontvangstbevestiging.versturen",
            () => {
              this.actionsSidenav.open();
              this.action = SideNavAction.ONTVANGSTBEVESTIGING;
            },
            "mark_email_read",
          ),
        );
      }

      this.menu.push(
        new ButtonMenuItem(
          "actie.mail.versturen",
          () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.MAIL_VERSTUREN;
          },
          "mail",
        ),
      );
    }

    if (this.zaak.rechten.wijzigen) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.document.maken",
          () => {
            this.maakDocument();
          },
          "note_add",
        ),
      );

      this.menu.push(
        new ButtonMenuItem(
          "actie.document.toevoegen",
          () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.DOCUMENT_TOEVOEGEN;
          },
          "upload_file",
        ),
      );

      this.menu.push(
        new ButtonMenuItem(
          "actie.document.verzenden",
          () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.DOCUMENT_VERZENDEN;
          },
          "local_post_office",
        ),
      );
    }

    if (
      this.zaak.isOpen &&
      !this.zaak.isInIntakeFase &&
      this.zaak.isBesluittypeAanwezig &&
      this.zaak.rechten.behandelen &&
      !this.zaak.isProcesGestuurd
    ) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.besluit.vastleggen",
          () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.BESLUIT_VASTLEGGEN;
          },
          "gavel",
        ),
      );
    }

    if (this.zaak.isHeropend && this.zaak.rechten.behandelen) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.zaak.afsluiten",
          () => this.openZaakAfsluitenDialog(),
          "thumb_up_alt",
        ),
      );
    }

    if (!this.zaak.isOpen && this.zaak.rechten.heropenen) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.zaak.heropenen",
          () => this.openZaakHeropenenDialog(),
          "restart_alt",
        ),
      );
    }

    forkJoin([
      this.planItemsService.listUserEventListenerPlanItems(this.zaak.uuid),
      this.planItemsService.listHumanTaskPlanItems(this.zaak.uuid),
      this.planItemsService.listProcessTaskPlanItems(this.zaak.uuid),
    ]).subscribe(
      ([
        userEventListenerPlanItems,
        humanTaskPlanItems,
        processTaskPlanItems,
      ]) => {
        if (
          this.zaak.rechten.behandelen &&
          userEventListenerPlanItems.length > 0
        ) {
          this.menu = this.menu.concat(
            userEventListenerPlanItems
              .map((userEventListenerPlanItem) =>
                this.createUserEventListenerPlanItemMenuItem(
                  userEventListenerPlanItem,
                ),
              )
              .filter((menuItem) => menuItem != null),
          );
        }
        if (
          this.zaak.isOpen &&
          !this.zaak.isHeropend &&
          this.zaak.rechten.afbreken &&
          this.zaak.zaaktype.zaakafhandelparameters.zaakbeeindigParameters
            .length > 0
        ) {
          this.menu.push(
            new ButtonMenuItem(
              "actie.zaak.afbreken",
              () => this.openZaakAfbrekenDialog(),
              "thumb_down_alt",
            ),
          );
        }
        if (this.hasZaakData()) {
          const title = this.zaak.rechten.wijzigenZaakdata
            ? "actie.zaakdata.wijzigen"
            : "actie.zaakdata.bekijken";
          this.menu.push(
            new ButtonMenuItem(
              title,
              () => {
                this.actionsSidenav.open();
                this.action = SideNavAction.ZAAKDATA_TONEN;
              },
              "folder_copy",
            ),
          );
        }
        if (this.zaak.rechten.behandelen && humanTaskPlanItems.length > 0) {
          this.menu.push(new HeaderMenuItem("actie.taak.starten"));
          this.menu = this.menu.concat(
            humanTaskPlanItems.map((humanTaskPlanItem) =>
              this.createHumanTaskPlanItemMenuItem(humanTaskPlanItem),
            ),
          );
        }
        if (this.zaak.rechten.behandelen && processTaskPlanItems.length > 0) {
          this.menu.push(new HeaderMenuItem("actie.proces.starten"));
          this.menu = this.menu.concat(
            processTaskPlanItems.map((processTaskPlanItem) =>
              this.createProcessTaskPlanItemMenuItem(processTaskPlanItem),
            ),
          );
        }
        this.createKoppelingenMenuItems();
        this.updateMargins();
      },
    );
  }

  private maakDocument(): void {
    const documentCreatieGegeven = new DocumentCreatieGegevens();
    documentCreatieGegeven.zaakUUID = this.zaak.uuid;
    this.informatieObjectenService
      .createDocument(documentCreatieGegeven)
      .subscribe((documentCreatieResponse) => {
        if (documentCreatieResponse.redirectURL) {
          window.open(documentCreatieResponse.redirectURL);
        } else {
          this.dialog.open(NotificationDialogComponent, {
            data: new NotificationDialogData(documentCreatieResponse.message),
          });
        }
      });
  }

  private createKoppelingenMenuItems(): void {
    if (this.zaak.rechten.behandelen || this.zaak.rechten.wijzigen) {
      this.menu.push(new HeaderMenuItem("koppelingen"));
      if (this.zaak.rechten.behandelen) {
        this.menu.push(
          new ButtonMenuItem(
            "actie.betrokkene.toevoegen",
            () => {
              this.actionsSidenav.open();
              this.action = SideNavAction.ZOEK_BETROKKENE;
            },
            "group_add",
          ),
        );
        this.menu.push(
          new ButtonMenuItem(
            "actie.bagObject.toevoegen",
            () => {
              this.actionsSidenav.open();
              this.action = SideNavAction.ZOEK_BAG_ADRES;
            },
            "add_home_work",
          ),
        );
      }
      if (this.zaak.rechten.wijzigen) {
        this.menu.push(
          new ButtonMenuItem(
            "actie.zaak.koppelen",
            () => {
              this.zaakKoppelenService.addTeKoppelenZaak(this.zaak);
            },
            "account_tree",
          ),
        );
      }
    }
  }

  openPlanItemStartenDialog(planItem: PlanItem): void {
    this.actionsSidenav.close();
    this.websocketService.doubleSuspendListener(this.zaakListener);
    const userEventListenerDialog =
      this.createUserEventListenerDialog(planItem);
    this.dialog
      .open(userEventListenerDialog.dialogComponent, {
        data: userEventListenerDialog.dialogData,
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          if (result === "openBesluitVastleggen") {
            this.actionsSidenav.open();
            this.action = SideNavAction.BESLUIT_VASTLEGGEN;
          } else {
            this.utilService.openSnackbar(
              "msg.planitem.uitgevoerd." + planItem.userEventListenerActie,
            );
            this.updateZaak();
          }
        }
      });
  }

  createUserEventListenerDialog(planItem: PlanItem): {
    dialogComponent: any;
    dialogData: any;
  } {
    switch (planItem.userEventListenerActie) {
      case UserEventListenerActie.IntakeAfronden:
        return this.createUserEventListenerIntakeAfrondenDialog(planItem);
      case UserEventListenerActie.ZaakAfhandelen:
        return this.createUserEventListenerZaakAfhandelenDialog(planItem);
      default:
        throw new Error(
          `Niet bestaande UserEventListenerActie: ${planItem.userEventListenerActie}`,
        );
    }
  }

  createUserEventListenerIntakeAfrondenDialog(planItem: PlanItem): {
    dialogComponent: any;
    dialogData: any;
  } {
    return {
      dialogComponent: IntakeAfrondenDialogComponent,
      dialogData: { zaak: this.zaak, planItem: planItem },
    };
  }

  createUserEventListenerZaakAfhandelenDialog(planItem: PlanItem): {
    dialogComponent: any;
    dialogData: any;
  } {
    return {
      dialogComponent: ZaakAfhandelenDialogComponent,
      dialogData: { zaak: this.zaak, planItem: planItem },
    };
  }

  private openZaakAfbrekenDialog(): void {
    this.actionsSidenav.close();
    const dialogData = new DialogData(
      [
        new SelectFormFieldBuilder()
          .id("reden")
          .label("actie.zaak.afbreken.reden")
          .optionLabel("naam")
          .options(
            this.zaakafhandelParametersService.listZaakbeeindigRedenenForZaaktype(
              this.zaak.zaaktype.uuid,
            ),
          )
          .validators(Validators.required)
          .build(),
      ],
      (results: any[]) =>
        this.zakenService
          .afbreken(this.zaak.uuid, results["reden"])
          .pipe(
            tap(() => this.websocketService.suspendListener(this.zaakListener)),
          ),
    );

    dialogData.confirmButtonActionKey = "actie.zaak.afbreken";

    this.dialog
      .open(DialogComponent, { data: dialogData })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.updateZaak();
          this.loadTaken();
          this.utilService.openSnackbar("msg.zaak.afgebroken");
        }
      });
  }

  private openZaakHeropenenDialog(): void {
    const dialogData = new DialogData(
      [
        new InputFormFieldBuilder()
          .id("reden")
          .label("actie.zaak.heropenen.reden")
          .validators(Validators.required)
          .maxlength(100)
          .build(),
      ],
      (results: any[]) =>
        this.zakenService
          .heropenen(this.zaak.uuid, results["reden"])
          .pipe(
            tap(() => this.websocketService.suspendListener(this.zaakListener)),
          ),
    );

    dialogData.confirmButtonActionKey = "actie.zaak.heropenen";

    this.dialog
      .open(DialogComponent, { data: dialogData })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.updateZaak();
          this.loadTaken();
          this.utilService.openSnackbar("msg.zaak.heropend");
        }
      });
  }

  private openZaakAfsluitenDialog(): void {
    this.actionsSidenav.close();
    const dialogData = new DialogData(
      [
        new SelectFormFieldBuilder()
          .id("resultaattype")
          .label("resultaat")
          .optionLabel("naam")
          .options(
            this.zakenService.listResultaattypes(this.zaak.zaaktype.uuid),
          )
          .validators(Validators.required)
          .build(),
        new InputFormFieldBuilder()
          .id("toelichting")
          .label("toelichting")
          .maxlength(80)
          .build(),
      ],
      (results: any[]) =>
        this.zakenService
          .afsluiten(
            this.zaak.uuid,
            results["toelichting"],
            results["resultaattype"].id,
          )
          .pipe(
            tap(() => this.websocketService.suspendListener(this.zaakListener)),
          ),
    );

    dialogData.confirmButtonActionKey = "actie.zaak.afsluiten";

    this.dialog
      .open(DialogComponent, { data: dialogData })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.updateZaak();
          this.loadTaken();
          this.utilService.openSnackbar("msg.zaak.afgesloten");
        }
      });
  }

  editDatumGroep(event: any): void {
    const zaak: Zaak = new Zaak();
    zaak.startdatum = event.startdatum;
    zaak.einddatumGepland = event.einddatumGepland;
    zaak.uiterlijkeEinddatumAfdoening = event.uiterlijkeEinddatumAfdoening;
    this.websocketService.suspendListener(this.zaakListener);
    this.zakenService
      .updateZaak(this.zaak.uuid, zaak, event.reden)
      .subscribe((updatedZaak) => {
        this.init(updatedZaak);
      });
  }

  editOpschorting(event: any): void {
    const zaakOpschortGegevens = new ZaakOpschortGegevens();
    zaakOpschortGegevens.indicatieOpschorting = !this.zaak.isOpgeschort;
    zaakOpschortGegevens.einddatumGepland = event.einddatumGepland;
    zaakOpschortGegevens.uiterlijkeEinddatumAfdoening =
      event.uiterlijkeEinddatumAfdoening;
    zaakOpschortGegevens.redenOpschorting = event.reden;
    zaakOpschortGegevens.duurDagen = event.duurDagen;
    this.websocketService.suspendListener(this.zaakListener);
    this.zakenService
      .opschortenZaak(this.zaak.uuid, zaakOpschortGegevens)
      .subscribe((updatedZaak) => {
        this.init(updatedZaak);
      });
  }

  editVerlenging(event: any): void {
    const zaakVerlengGegevens = new ZaakVerlengGegevens();
    zaakVerlengGegevens.einddatumGepland = event.einddatumGepland;
    zaakVerlengGegevens.uiterlijkeEinddatumAfdoening =
      event.uiterlijkeEinddatumAfdoening;
    zaakVerlengGegevens.redenVerlenging = event.reden;
    zaakVerlengGegevens.duurDagen = event.duurDagen;
    zaakVerlengGegevens.takenVerlengen = event.takenVerlengen;
    this.websocketService.suspendListener(this.zaakListener);
    this.zakenService
      .verlengenZaak(this.zaak.uuid, zaakVerlengGegevens)
      .subscribe((updatedZaak) => {
        this.init(updatedZaak);
      });
  }

  private loadOpschorting(): void {
    if (this.zaak.isOpgeschort) {
      this.zakenService
        .readOpschortingZaak(this.zaak.uuid)
        .subscribe((objectData) => {
          this.zaakOpschorting = objectData;
        });
    }
  }

  editToewijzing(event: any) {
    if (
      event["medewerker-groep"].medewerker &&
      event["medewerker-groep"].medewerker.id === this.ingelogdeMedewerker.id &&
      this.zaak.groep === event["medewerker-groep"].groep
    ) {
      this.assignZaakToMe(event);
    } else {
      this.zaak.groep = event["medewerker-groep"].groep;
      this.zaak.behandelaar = event["medewerker-groep"].medewerker;
      this.websocketService.doubleSuspendListener(this.zaakRollenListener);
      this.zakenService.toekennen(this.zaak, event.reden).subscribe((zaak) => {
        if (this.zaak.behandelaar) {
          this.utilService.openSnackbar("msg.zaak.toegekend", {
            behandelaar: this.zaak.behandelaar.naam,
          });
        } else {
          this.utilService.openSnackbar("msg.vrijgegeven.zaak");
        }
        this.init(zaak);
      });
    }
  }

  editZaakMetReden(event: any, field: string): void {
    const zaak: Zaak = new Zaak();
    zaak[field] = event[field].value ? event[field].value : event[field];
    this.websocketService.suspendListener(this.zaakListener);
    this.zakenService
      .updateZaak(this.zaak.uuid, zaak, event.reden)
      .subscribe((updatedZaak) => {
        this.init(updatedZaak);
      });
  }

  editZaak(value: string, field: string): void {
    const zaak: Zaak = new Zaak();
    zaak[field] = value;
    this.websocketService.suspendListener(this.zaakListener);
    this.zakenService
      .updateZaak(this.zaak.uuid, zaak)
      .subscribe((updatedZaak) => {
        this.init(updatedZaak);
      });
  }

  public updateZaak(): void {
    this.zakenService.readZaak(this.zaak.uuid).subscribe((zaak) => {
      this.init(zaak);
    });
  }

  private loadHistorie(): void {
    this.zakenService
      .listHistorieVoorZaak(this.zaak.uuid)
      .subscribe((historie) => {
        this.historie.data = historie;
      });
  }

  private loadBetrokkenen(): void {
    this.zakenService
      .listBetrokkenenVoorZaak(this.zaak.uuid)
      .subscribe((betrokkenen) => {
        this.betrokkenen.data = betrokkenen;
      });
  }

  private loadBagObjecten(): void {
    this.bagService.list(this.zaak.uuid).subscribe((bagObjecten) => {
      this.gekoppeldeBagObjecten = bagObjecten.map((bg) => bg.zaakobject);
      this.bagObjectenDataSource.data = bagObjecten;
    });
  }

  showOrEditZaakLocatie(): void {
    if (
      (this.zaak.isOpen && this.zaak.rechten.wijzigen) ||
      this.zaak.zaakgeometrie != null
    ) {
      this.action = SideNavAction.ZOEK_LOCATIE;
      this.actionsSidenav.open();
    }
  }

  addOrEditZaakInitiator(): void {
    this.action = SideNavAction.ZOEK_INITIATOR;
    this.actionsSidenav.open();
  }

  private loadBesluiten(): void {
    this.zakenService
      .listBesluitenForZaak(this.zaak.uuid)
      .subscribe((besluiten) => (this.zaak.besluiten = besluiten));
  }

  private loadTaken(): void {
    this.takenLoading = true;
    this.takenService
      .listTakenVoorZaak(this.zaak.uuid)
      .pipe(
        map((values) => values.map((value) => new ExpandableTableData(value))),
      )
      .subscribe((taken) => {
        taken = taken.sort(
          (a, b) =>
            a.data.fataledatum?.localeCompare(b.data.fataledatum) ||
            a.data.creatiedatumTijd?.localeCompare(b.data.creatiedatumTijd),
        );
        this.takenDataSource.data = taken;
        this.filterTakenOpStatus();
        this.takenLoading = false;
      });
  }

  expandTaken(expand: boolean): void {
    this.takenDataSource.data.forEach((value) => (value.expanded = expand));
    this.checkAllTakenExpanded();
  }

  expandTaak(taak: ExpandableTableData<Taak>): void {
    taak.expanded = !taak.expanded;
    this.checkAllTakenExpanded();
  }

  checkAllTakenExpanded(): void {
    const filter: ExpandableTableData<Taak>[] = this.toonAfgerondeTaken
      ? this.takenDataSource.data.filter((value) => !value.expanded)
      : this.takenDataSource.data.filter(
          (value) => value.data.status !== "AFGEROND" && !value.expanded,
        );

    this.allTakenExpanded = filter.length === 0;
  }

  showAssignTaakToMe(taak: Taak): boolean {
    return (
      taak.status !== TaakStatus.Afgerond &&
      taak.rechten.toekennen &&
      this.ingelogdeMedewerker.id !== taak.behandelaar?.id &&
      this.ingelogdeMedewerker.groupIds.indexOf(taak.groep.id) >= 0
    );
  }

  private assignZaakToMe(event: any): void {
    this.websocketService.doubleSuspendListener(this.zaakRollenListener);
    this.zakenService
      .toekennenAanIngelogdeMedewerker(this.zaak, event.reden)
      .subscribe((zaak) => {
        this.init(zaak);
        this.utilService.openSnackbar("msg.zaak.toegekend", {
          behandelaar: zaak.behandelaar?.naam,
        });
      });
  }

  locatieGeselecteerd(locatie: GeometryGegevens): void {
    this.actionsSidenav.close();
    this.websocketService.suspendListener(this.zaakListener);
    this.zakenService
      .updateZaakLocatie(this.zaak.uuid, locatie.geometry, locatie.reden)
      .subscribe((updatedZaak) => {
        this.init(updatedZaak);
      });
  }

  initiatorGeselecteerd(initiator: Klant): void {
    this.websocketService.suspendListener(this.zaakRollenListener);
    this.actionsSidenav.close();
    const melding = this.zaak.initiatorIdentificatie
      ? "msg.initiator.gewijzigd"
      : "msg.initiator.toegevoegd";
    this.zakenService
      .updateInitiator(this.zaak, initiator)
      .subscribe((zaak) => {
        this.zaak = zaak;
        this.utilService.openSnackbar(melding, {
          naam: zaak.initiatorIdentificatie,
        });
        this.loadHistorie();
      });
  }

  deleteInitiator(): void {
    this.websocketService.suspendListener(this.zaakRollenListener);
    this.dialog
      .open(DialogComponent, {
        data: new DialogData(
          [
            new TextareaFormFieldBuilder()
              .id("reden")
              .label("reden")
              .validators(Validators.required)
              .build(),
          ],
          (results: any[]) =>
            this.zakenService.deleteInitiator(this.zaak, results["reden"]),
          this.translate.instant("msg.initiator.ontkoppelen.bevestigen"),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar("msg.initiator.ontkoppelen.uitgevoerd");
          this.zakenService.readZaak(this.zaak.uuid).subscribe((zaak) => {
            this.zaak = zaak;
            this.loadHistorie();
          });
        }
      });
  }

  betrokkeneGeselecteerd(betrokkene: KlantGegevens): void {
    this.websocketService.suspendListener(this.zaakRollenListener);
    this.actionsSidenav.close();
    this.zakenService
      .createBetrokkene(
        this.zaak,
        betrokkene.klant,
        betrokkene.betrokkeneRoltype,
        betrokkene.betrokkeneToelichting,
      )
      .subscribe((zaak) => {
        this.zaak = zaak;
        this.utilService.openSnackbar("msg.betrokkene.toegevoegd", {
          roltype: betrokkene.betrokkeneRoltype.naam,
        });
        this.loadHistorie();
        this.loadBetrokkenen();
      });
  }

  deleteBetrokkene(betrokkene: ZaakBetrokkene): void {
    this.websocketService.suspendListener(this.zaakRollenListener);
    const betrokkeneIdentificatie: string =
      betrokkene.roltype + " " + betrokkene.identificatie;
    this.dialog
      .open(DialogComponent, {
        data: new DialogData(
          [
            new TextareaFormFieldBuilder()
              .id("reden")
              .label("reden")
              .validators(Validators.required)
              .build(),
          ],
          (results: any[]) =>
            this.zakenService.deleteBetrokkene(
              betrokkene.rolid,
              results["reden"],
            ),
          this.translate.instant("msg.betrokkene.ontkoppelen.bevestigen", {
            betrokkene: betrokkeneIdentificatie,
          }),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar(
            "msg.betrokkene.ontkoppelen.uitgevoerd",
            { betrokkene: betrokkeneIdentificatie },
          );
          this.zakenService.readZaak(this.zaak.uuid).subscribe((zaak) => {
            this.zaak = zaak;
            this.loadHistorie();
            this.loadBetrokkenen();
          });
        }
      });
  }

  adresGeselecteerd(bagObject: BAGObject): void {
    this.websocketService.suspendListener(this.zaakListener);
    this.bagService
      .create(new BAGObjectGegevens(this.zaak.uuid, bagObject))
      .subscribe(() => {
        this.utilService.openSnackbar("msg.bagObject.toegevoegd");
        this.loadHistorie();
        this.loadBagObjecten();
      });
  }

  assignTaakToMe(taak: Taak, $event) {
    $event.stopPropagation();

    this.websocketService.suspendListener(this.zaakTakenListener);
    this.takenService
      .toekennenAanIngelogdeMedewerker(taak)
      .subscribe((returnTaak) => {
        taak.behandelaar = returnTaak.behandelaar;
        taak.status = returnTaak.status;
        this.utilService.openSnackbar("msg.taak.toegekend", {
          behandelaar: taak.behandelaar.naam,
        });
      });
  }

  filterTakenOpStatus() {
    if (!this.toonAfgerondeTaken) {
      this.takenFilter["status"] = "AFGEROND";
    }

    this.takenDataSource.filter = this.takenFilter;
    SessionStorageUtil.setItem("toonAfgerondeTaken", this.toonAfgerondeTaken);
  }

  taakGestart(): void {
    this.actiefPlanItem = null;
    this.sluitSidenav();
    this.updateZaak();
  }

  processGestart(): void {
    this.actiefPlanItem = null;
    this.sluitSidenav();
    this.updateZaak();
  }

  sluitSidenav(): void {
    this.action = null;
    this.actionsSidenav.close();
  }

  mailVerstuurd(mailVerstuurd: boolean): void {
    this.sluitSidenav();
    if (mailVerstuurd) {
      this.updateZaak();
    }
  }

  ontvangstBevestigd(ontvangstBevestigd: boolean): void {
    this.sluitSidenav();
    if (ontvangstBevestigd) {
      this.updateZaak();
    }
  }

  documentToegevoegd(): void {
    this.updateZaak();
  }

  startZaakOntkoppelenDialog(gerelateerdeZaak: GerelateerdeZaak): void {
    const zaakOntkoppelGegevens: ZaakOntkoppelGegevens =
      new ZaakOntkoppelGegevens();
    zaakOntkoppelGegevens.zaakUuid = this.zaak.uuid;
    zaakOntkoppelGegevens.gekoppeldeZaakIdentificatie =
      gerelateerdeZaak.identificatie;
    zaakOntkoppelGegevens.relatietype = gerelateerdeZaak.relatieType;

    this.dialog
      .open(ZaakOntkoppelenDialogComponent, {
        data: zaakOntkoppelGegevens,
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar("msg.zaak.ontkoppelen.uitgevoerd");
        }
      });
  }

  besluitVastgelegd(): void {
    this.sluitSidenav();
  }

  besluitWijzigen($event): void {
    this.action = SideNavAction.BESLUIT_WIJZIGEN;
    this.teWijzigenBesluit = $event;
    this.actionsSidenav.open();
  }

  doIntrekking($event): void {
    const gegevens = new BesluitIntrekkenGegevens();
    gegevens.besluitUuid = $event.uuid;
    gegevens.vervaldatum = $event.vervaldatum;
    gegevens.vervalreden = $event.vervalreden.value;
    gegevens.reden = $event.toelichting;
    this.zakenService.intrekkenBesluit(gegevens).subscribe(() => {
      this.utilService.openSnackbar("msg.besluit.ingetrokken");
    });
  }

  betrokkeneGegevensOphalen(betrokkene: ZaakBetrokkene): void {
    betrokkene["gegevens"] = "LOADING";
    switch (betrokkene.type) {
      case "NATUURLIJK_PERSOON":
        this.klantenService
          .readPersoon(betrokkene.identificatie)
          .subscribe((persoon) => {
            betrokkene["gegevens"] = persoon.naam;
            if (persoon.geboortedatum) {
              betrokkene["gegevens"] += `, ${this.datumPipe.transform(
                persoon.geboortedatum,
              )}`;
            }
            if (persoon.verblijfplaats) {
              betrokkene["gegevens"] += `,\n${persoon.verblijfplaats}`;
            }
          });
        break;
      case "NIET_NATUURLIJK_PERSOON":
      case "VESTIGING":
        this.klantenService
          .readBedrijf(betrokkene.identificatie)
          .subscribe((bedrijf) => {
            betrokkene["gegevens"] = bedrijf.naam;
            if (bedrijf.adres) {
              betrokkene["gegevens"] += `,\n${bedrijf.adres}`;
            }
          });
        break;
      case "ORGANISATORISCHE_EENHEID":
      case "MEDEWERKER":
        betrokkene["gegevens"] = "-";
        break;
    }
  }

  bagObjectVerwijderen(bagObjectGegevens: BAGObjectGegevens): void {
    const bagObject = bagObjectGegevens.zaakobject;
    const reden = new InputFormFieldBuilder()
      .maxlength(80)
      .id("reden")
      .label("reden")
      .validators(Validators.required)
      .build();
    const dialogData = new DialogData([reden], (results: any[]) =>
      this.bagService
        .delete(
          new BAGObjectGegevens(
            this.zaak.uuid,
            bagObject,
            bagObjectGegevens.uuid,
            results["reden"],
          ),
        )
        .pipe(
          tap(() => this.websocketService.suspendListener(this.zaakListener)),
        ),
    );

    dialogData.uitleg = this.translate.instant(
      "msg.bagObject.verwijderen.bevestigen",
      { omschrijving: bagObject.omschrijving },
    );
    dialogData.confirmButtonActionKey = "actie.bagObject.verwijderen";
    this.dialog
      .open(DialogComponent, { data: dialogData })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.loadHistorie();
          this.loadBagObjecten();
          this.utilService.openSnackbar(
            "msg.bagObject.verwijderen.uitgevoerd",
            { omschrijving: bagObject.omschrijving },
          );
        }
      });
  }

  showProces() {
    const dialogData = new DialogData([
      new ReadonlyFormFieldBuilder(
        '<img src="/rest/zaken/' +
          this.zaak.uuid +
          '/procesdiagram"/ alt="diagram">',
      )
        .id("diagram")
        .label("proces.toestand")
        .build(),
    ]);
    dialogData.confirmButtonActionKey = "actie.ok";
    dialogData.cancelButtonActionKey = null;
    this.dialog.open(DialogComponent, { data: dialogData });
  }

  hasZaakData() {
    return this.zaak.zaakdata && Object.keys(this.zaak.zaakdata).length > 0;
  }
}
