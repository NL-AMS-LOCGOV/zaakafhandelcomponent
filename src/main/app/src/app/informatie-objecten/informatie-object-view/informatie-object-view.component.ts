/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { EnkelvoudigInformatieobject } from "../model/enkelvoudig-informatieobject";
import { MenuItem } from "../../shared/side-nav/menu-item/menu-item";
import { InformatieObjectenService } from "../informatie-objecten.service";
import { ActivatedRoute, Router } from "@angular/router";
import { UtilService } from "../../core/service/util.service";
import { ZaakInformatieobject } from "../model/zaak-informatieobject";
import { HrefMenuItem } from "../../shared/side-nav/menu-item/href-menu-item";
import { HeaderMenuItem } from "../../shared/side-nav/menu-item/header-menu-item";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { WebsocketService } from "../../core/websocket/websocket.service";
import { Opcode } from "../../core/websocket/model/opcode";
import { ObjectType } from "../../core/websocket/model/object-type";
import { WebsocketListener } from "../../core/websocket/model/websocket-listener";
import { MatTableDataSource } from "@angular/material/table";
import { HistorieRegel } from "../../shared/historie/model/historie-regel";
import { MatSort } from "@angular/material/sort";
import { FileFormatUtil } from "../model/file-format";
import { ButtonMenuItem } from "../../shared/side-nav/menu-item/button-menu-item";
import { SideNavAction } from "../../shared/side-nav/side-nav-action";
import { ActionsViewComponent } from "../../shared/abstract-view/actions-view-component";
import { EnkelvoudigInformatieObjectVersieGegevens } from "../model/enkelvoudig-informatie-object-versie-gegevens";
import { TranslateService } from "@ngx-translate/core";
import { FileIcon } from "../model/file-icon";
import { Zaak } from "../../zaken/model/zaak";
import { DialogData } from "../../shared/dialog/dialog-data";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { DialogComponent } from "../../shared/dialog/dialog.component";
import { Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { tap } from "rxjs/operators";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../../shared/confirm-dialog/confirm-dialog.component";
import { Observable } from "rxjs";
import { IndicatiesLayout } from "../../shared/indicaties/indicaties.component";
import { InformatieobjectStatus } from "../model/informatieobject-status.enum";
import { ZakenService } from "../../zaken/zaken.service";

@Component({
  templateUrl: "./informatie-object-view.component.html",
  styleUrls: ["./informatie-object-view.component.less"],
})
export class InformatieObjectViewComponent
  extends ActionsViewComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  readonly indicatiesLayout = IndicatiesLayout;
  infoObject: EnkelvoudigInformatieobject;
  laatsteVersieInfoObject: EnkelvoudigInformatieobject;
  zaakInformatieObjecten: ZaakInformatieobject[];
  zaak: Zaak;
  documentNieuweVersieGegevens: EnkelvoudigInformatieObjectVersieGegevens;
  documentPreviewBeschikbaar = false;
  menu: MenuItem[];
  readonly sideNavAction = SideNavAction;
  action: SideNavAction;
  versieInformatie: string;
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

  @ViewChild("actionsSidenav") actionsSidenav: MatSidenav;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild(MatSort) sort: MatSort;
  private documentListener: WebsocketListener;
  private converteerButton: ButtonMenuItem = null;

  constructor(
    private informatieObjectenService: InformatieObjectenService,
    private route: ActivatedRoute,
    public utilService: UtilService,
    private websocketService: WebsocketService,
    private router: Router,
    private translate: TranslateService,
    private dialog: MatDialog,
    private zakenService: ZakenService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.subscriptions$.push(
      this.route.data.subscribe((data) => {
        this.infoObject = data["informatieObject"];
        this.zaak = data["zaak"];
        this.informatieObjectenService
          .readEnkelvoudigInformatieobject(
            this.infoObject.uuid,
            this.zaak?.uuid,
          )
          .subscribe((infoObject) => {
            this.laatsteVersieInfoObject = infoObject;
            this.toevoegenActies();
            this.updateVersieInformatie();
            this.loadZaakInformatieobjecten();
          });
        this.documentPreviewBeschikbaar = FileFormatUtil.isPreviewAvailable(
          this.infoObject.formaat,
        );
        this.utilService.setTitle("title.document", {
          document: this.infoObject.identificatie,
        });

        this.documentListener = this.websocketService.addListener(
          Opcode.UPDATED,
          ObjectType.ENKELVOUDIG_INFORMATIEOBJECT,
          this.infoObject.uuid,
          () => {
            this.loadInformatieObject();
            this.toevoegenActies();
            this.loadZaakInformatieobjecten();
            this.loadHistorie();
            this.resetConvertKnop();
          },
        );

        this.loadHistorie();
      }),
    );
  }

  ngAfterViewInit() {
    super.ngAfterViewInit();
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
    this.historie.sort = this.sort;
  }

  ngOnDestroy() {
    this.websocketService.removeListener(this.documentListener);
  }

  private toevoegenActies() {
    this.menu = [new HeaderMenuItem("informatieobject")];

    if (this.laatsteVersieInfoObject.rechten.lezen) {
      this.menu.push(
        new HrefMenuItem(
          "actie.downloaden",
          this.informatieObjectenService.getDownloadURL(
            this.infoObject.uuid,
            this.infoObject.versie,
          ),
          "save_alt",
        ),
      );
    }

    if (this.laatsteVersieInfoObject.rechten.wijzigen) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.nieuwe.versie.toevoegen",
          () => {
            this.informatieObjectenService
              .readHuidigeVersieEnkelvoudigInformatieObject(
                this.infoObject.uuid,
              )
              .subscribe((nieuweVersie) => {
                this.documentNieuweVersieGegevens = nieuweVersie;
                this.actionsSidenav.open();
                this.action = SideNavAction.DOCUMENT_VERSIE_TOEVOEGEN;
              });
          },
          "difference",
        ),
      );
    }

    if (
      this.laatsteVersieInfoObject.rechten.wijzigen &&
      FileFormatUtil.isOffice(this.infoObject.formaat)
    ) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.bewerken",
          () => {
            this.informatieObjectenService
              .editEnkelvoudigInformatieObjectInhoud(
                this.infoObject.uuid,
                this.zaak?.uuid,
              )
              .subscribe((url) => {
                window.open(url);
              });
          },
          "edit",
        ),
      );
    }

    if (
      !this.laatsteVersieInfoObject.gelockedDoor &&
      this.laatsteVersieInfoObject.rechten.vergrendelen
    ) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.lock",
          () => {
            this.informatieObjectenService
              .lockInformatieObject(this.infoObject.uuid, this.zaak?.uuid)
              .subscribe(() => {});
          },
          "lock",
        ),
      );
    }

    if (
      this.laatsteVersieInfoObject.gelockedDoor &&
      this.laatsteVersieInfoObject.rechten.ontgrendelen
    ) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.unlock",
          () => {
            this.informatieObjectenService
              .unlockInformatieObject(this.infoObject.uuid, this.zaak?.uuid)
              .subscribe(() => {});
          },
          "lock_open",
        ),
      );
    }

    if (
      this.laatsteVersieInfoObject.rechten.verwijderen &&
      !this.laatsteVersieInfoObject.isBesluitDocument
    ) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.verwijderen",
          () => this.openDocumentVerwijderenDialog(),
          "delete",
        ),
      );
    }

    if (
      !this.laatsteVersieInfoObject.ondertekening &&
      this.laatsteVersieInfoObject.rechten.ondertekenen
    ) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.ondertekenen",
          () => this.openDocumentOndertekenenDialog(),
          "fact_check",
        ),
      );
    }

    if (
      this.infoObject.status === InformatieobjectStatus.DEFINITIEF &&
      this.laatsteVersieInfoObject.rechten.wijzigen &&
      FileFormatUtil.isOffice(this.infoObject.formaat)
    ) {
      this.converteerButton = new ButtonMenuItem(
        "actie.converteren",
        () => {
          this.converteerButton.disabled = true;
          this.utilService.setLoading(true);
          this.informatieObjectenService
            .convertInformatieObjectToPDF(this.infoObject.uuid, this.zaak?.uuid)
            .subscribe(() => {});
        },
        "picture_as_pdf",
      );
      this.menu.push(this.converteerButton);
    }
  }

  private loadZaakInformatieobjecten(): void {
    this.informatieObjectenService
      .listZaakInformatieobjecten(this.infoObject.uuid)
      .subscribe((zaakInformatieObjecten) => {
        this.zaakInformatieObjecten = zaakInformatieObjecten;
        this.loadZaak();
      });
  }

  private loadHistorie(): void {
    this.informatieObjectenService
      .listHistorie(this.infoObject.uuid)
      .subscribe((historie) => {
        this.historie.data = historie;
      });
  }

  private loadInformatieObject() {
    this.informatieObjectenService
      .readEnkelvoudigInformatieobject(this.infoObject.uuid, this.zaak?.uuid)
      .subscribe((infoObject) => {
        this.infoObject = infoObject;
        this.laatsteVersieInfoObject = infoObject;
        this.toevoegenActies();
        this.updateVersieInformatie();
        this.documentPreviewBeschikbaar = FileFormatUtil.isPreviewAvailable(
          this.infoObject.formaat,
        );
      });
  }

  haalVersieOp(versie: number) {
    this.websocketService.removeListener(this.documentListener);
    this.router.navigate([
      "/informatie-objecten",
      this.infoObject.uuid,
      versie,
      this.zaak.uuid,
    ]);
  }

  versieToegevoegd(informatieobject: EnkelvoudigInformatieobject): void {
    this.haalVersieOp(informatieobject.versie);
  }

  getFileIcon(filename) {
    return FileIcon.getIconByBestandsnaam(filename);
  }

  getFileTooltip(filetype: string): string {
    return this.translate.instant("bestandstype", {
      type: filetype.toUpperCase(),
    });
  }

  private updateVersieInformatie(): void {
    this.versieInformatie = this.translate.instant("versie.x.van", {
      versie: this.infoObject.versie,
      laatsteVersie: this.laatsteVersieInfoObject.versie,
    });
  }

  private openDocumentVerwijderenDialog(): void {
    const dialogData = new DialogData(
      this.zaak
        ? [
            new InputFormFieldBuilder()
              .id("reden")
              .label("actie.document.verwijderen.reden")
              .validators(Validators.required)
              .maxlength(100)
              .build(),
          ]
        : [],
      (results: any[]) =>
        this.deleteEnkelvoudigInformatieObject$(results["reden"]),
      this.translate.instant("msg.document.verwijderen.bevestigen", {
        document: this.infoObject.titel,
      }),
    );

    dialogData.confirmButtonActionKey = "actie.document.verwijderen";

    this.dialog
      .open(DialogComponent, { data: dialogData })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar("msg.document.verwijderen.uitgevoerd", {
            document: this.infoObject.titel,
          });
          this.router.navigate(
            this.zaak
              ? ["/zaken", this.zaak.identificatie]
              : ["/documenten", "ontkoppelde"],
          );
        }
      });
  }

  private openDocumentOndertekenenDialog(): void {
    const dialogData = new ConfirmDialogData(
      {
        key: "msg.document.ondertekenen.bevestigen",
        args: { document: this.infoObject.titel },
      },
      this.informatieObjectenService.ondertekenInformatieObject(
        this.infoObject.uuid,
        this.zaak.uuid,
      ),
    );

    this.dialog.open(ConfirmDialogComponent, { data: dialogData });
  }

  private deleteEnkelvoudigInformatieObject$(reden?: string): Observable<void> {
    return this.informatieObjectenService
      .deleteEnkelvoudigInformatieObject(
        this.infoObject.uuid,
        this.zaak?.uuid,
        reden,
      )
      .pipe(
        tap(() => this.websocketService.suspendListener(this.documentListener)),
      );
  }

  private resetConvertKnop(): void {
    this.converteerButton.disabled = false;
    this.utilService.setLoading(false);
  }

  /**
   * Voor het geval dat er bij navigatie naar het enkelvoudiginformatieobject geen zaak meegegeven is,
   * dan wordt deze via de verkorte zaak gegevens opgehaald.
   *
   * Als er ook geen verkorte zaak gegevens beschikbaar, dan is dit een document zonder zaak.
   */
  private loadZaak(): void {
    if (
      !this.zaak &&
      this.zaakInformatieObjecten &&
      this.zaakInformatieObjecten.length > 0
    ) {
      this.zakenService
        .readZaakByID(this.zaakInformatieObjecten[0].zaakIdentificatie)
        .subscribe((zaak) => {
          this.zaak = zaak;
        });
    }
  }
}
