/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Taak} from '../model/taak';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {ActivatedRoute} from '@angular/router';
import {TakenService} from '../taken.service';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {IdentityService} from '../../identity/identity.service';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {
    TextareaFormFieldBuilder
} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {User} from '../../identity/model/user';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {TranslateService} from '@ngx-translate/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {TaakHistorieRegel} from '../../shared/historie/model/taak-historie-regel';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {SideNavAction} from '../../shared/side-nav/side-nav-action';
import {ActionsViewComponent} from '../../shared/abstract-view/actions-view-component';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {TaakStatus} from '../model/taak-status.enum';
import {
    MedewerkerGroepFieldBuilder
} from '../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-field-builder';
import {Zaak} from '../../zaken/model/zaak';
import {ZakenService} from '../../zaken/zaken.service';
import {DocumentCreatieGegevens} from '../../informatie-objecten/model/document-creatie-gegevens';
import {
    NotificationDialogComponent,
    NotificationDialogData
} from '../../shared/notification-dialog/notification-dialog.component';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {MatDialog} from '@angular/material/dialog';
import {Zaaktype} from '../../zaken/model/zaaktype';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {FormulierDefinitie} from '../../admin/model/formulieren/formulier-definitie';

@Component({
    templateUrl: './taak-view.component.html',
    styleUrls: ['./taak-view.component.less']
})
export class TaakViewComponent extends ActionsViewComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild('actionsSidenav') actionsSidenav: MatSidenav;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;
    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('historieSort') historieSort: MatSort;

    taak: Taak;
    zaak: Zaak;
    formulierDefinitie: FormulierDefinitie;

    menu: MenuItem[] = [];
    readonly sideNavAction = SideNavAction;
    action: SideNavAction;

    historieSrc: MatTableDataSource<TaakHistorieRegel> = new MatTableDataSource<TaakHistorieRegel>();
    historieColumns: string[] = ['datum', 'wijziging', 'oudeWaarde', 'nieuweWaarde', 'toelichting'];

    editFormFields: Map<string, any> = new Map<string, any>();
    fataledatumIcon: TextIcon;
    initialized = false;

    posts: number = 0;
    private taakListener: WebsocketListener;
    private ingelogdeMedewerker: User;
    readonly TaakStatusAfgerond = TaakStatus.Afgerond;

    constructor(private route: ActivatedRoute,
                private takenService: TakenService,
                private zakenService: ZakenService,
                private informatieObjectenService: InformatieObjectenService,
                public utilService: UtilService,
                private dialog: MatDialog,
                private websocketService: WebsocketService,
                private identityService: IdentityService,
                protected translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        this.getIngelogdeMedewerker();
        this.route.data.subscribe(data => {
            this.createZaakFromTaak(data.taak);
            this.init(data.taak, true);
        });
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit();

        this.taakListener = this.websocketService.addListenerWithSnackbar(
            Opcode.ANY, ObjectType.TAAK, this.taak.id,
            () => this.reloadTaak());

        this.historieSrc.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'datum':
                    return item.datumTijd;
                default:
                    return item[property];
            }
        };
        this.historieSrc.sort = this.historieSort;
    }

    ngOnDestroy() {
        super.ngOnDestroy();
        this.websocketService.removeListener(this.taakListener);
    }

    private initTaakGegevens(taak: Taak): void {
        this.menu = [];
        this.taak = taak;
        this.loadHistorie();
        this.setEditableFormFields();
        this.setupMenu();
    }

    private init(taak: Taak, initZaak: boolean): void {
        this.initTaakGegevens(taak);
        if (initZaak) {
            this.zakenService.readZaak(this.taak.zaakUuid).subscribe(zaak => {
                this.zaak = zaak;
                this.createTaakForm(taak);
                this.initialized = true;
            });
        }

    }

    private createTaakForm(taak: Taak): void {
        if (taak.formulierDefinitie) {
            this.formulierDefinitie = taak.formulierDefinitie;
            this.utilService.setTitle('title.taak', {taak: this.formulierDefinitie.naam});
        }
    }

    isReadonly() {
        return this.taak.status === TaakStatus.Afgerond || !this.taak.rechten.wijzigen;
    }

    private setEditableFormFields(): void {
        this.editFormFields.set('medewerker-groep',
                new MedewerkerGroepFieldBuilder(this.taak.groep, this.taak.behandelaar)
                        .id('medewerker-groep')
                        .groepLabel('groep.-kies-')
                        .groepRequired()
                        .medewerkerLabel('behandelaar.-kies-')
                        .build());
        this.editFormFields.set('toelichting',
                new TextareaFormFieldBuilder(this.taak.toelichting)
                        .id('toelichting')
                        .label('toelichting')
                        .maxlength(1000)
                        .build());
        this.editFormFields.set('reden', new InputFormFieldBuilder().id('reden')
                                                                    .label('reden')
                                                                    .maxlength(80)
                                                                    .build());

        this.fataledatumIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem', 'errorTaakVerlopen_icon',
            'msg.datum.overschreden', 'error');
    }

    private setupMenu(): void {
        this.menu.push(new HeaderMenuItem('taak'));

        if (this.taak.rechten.wijzigen) {
            this.menu.push(new ButtonMenuItem('actie.document.maken', () => {
                this.maakDocument();
            }, 'note_add'));

            if (this.taak.status !== TaakStatus.Afgerond) {
                this.menu.push(new ButtonMenuItem('actie.document.toevoegen', () => {
                    this.actionsSidenav.open();
                    this.action = SideNavAction.DOCUMENT_TOEVOEGEN;
                }, 'upload_file'));
            }
        }
    }

    private maakDocument(): void {
        const documentCreatieGegeven = new DocumentCreatieGegevens();
        documentCreatieGegeven.zaakUUID = this.taak.zaakUuid;
        documentCreatieGegeven.taskId = this.taak.id;
        this.informatieObjectenService.createDocument(documentCreatieGegeven)
            .subscribe((documentCreatieResponse) => {
                if (documentCreatieResponse.redirectURL) {
                    window.open(documentCreatieResponse.redirectURL);
                } else {
                    this.dialog.open(NotificationDialogComponent,
                        {data: new NotificationDialogData(documentCreatieResponse.message)});
                }
            });
    }

    private loadHistorie(): void {
        this.takenService.listHistorieVoorTaak(this.taak.id).subscribe(historie => {
            this.historieSrc.data = historie;
        });
    }

    onFormSubmit(formState: {}): void {
        if (formState) {
            this.taak.taakdata = formState;
            this.websocketService.suspendListener(this.taakListener);
            this.takenService.complete(this.taak).subscribe(taak => {
                this.utilService.openSnackbar('msg.taak.afgerond');
                this.init(taak, true);
            });
        }
    }

    editToewijzing(event: any) {
        if (event['medewerker-groep'].medewerker && event['medewerker-groep'].medewerker.id === this.ingelogdeMedewerker.id &&
                this.taak.groep === event['medewerker-groep'].groep) {
            this.assignToMe();
        } else {
            this.taak.groep = event['medewerker-groep'].groep;
            this.taak.behandelaar = event['medewerker-groep'].medewerker;
            const reden: string = event['reden'];
            this.websocketService.suspendListener(this.taakListener);
            this.takenService.toekennen(this.taak, reden).subscribe(() => {
                if (this.taak.behandelaar) {
                    this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: this.taak.behandelaar.naam});
                } else {
                    this.utilService.openSnackbar('msg.vrijgegeven.taak');
                }
                this.init(this.taak, false);
            });
        }
    }

    partialEditTaak(value: string, field: string): void {
        this.taak[field] = value[field];
        this.websocketService.suspendListener(this.taakListener);
        this.takenService.update(this.taak).subscribe((taak) => {
            this.utilService.openSnackbar('msg.taak.opgeslagen');
            this.initTaakGegevens(taak);
        });
    }

    private reloadTaak() {
        this.takenService.readTaak(this.taak.id).subscribe(taak => {
            this.init(taak, true);
        });
    }

    private getIngelogdeMedewerker() {
        this.identityService.readLoggedInUser().subscribe(ingelogdeMedewerker => {
            this.ingelogdeMedewerker = ingelogdeMedewerker;
        });
    }

    private assignToMe(): void {
        this.websocketService.suspendListener(this.taakListener);
        this.takenService.toekennenAanIngelogdeMedewerker(this.taak).subscribe(taak => {
            this.taak.behandelaar = taak.behandelaar;
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taak.behandelaar.naam});
            this.init(this.taak, false);
        });
    }

    updateTaakdocumenten(informatieobject: EnkelvoudigInformatieobject) {
        if (!this.taak.taakdocumenten) {
            this.taak.taakdocumenten = [];
        }
        this.taak.taakdocumenten.push(informatieobject.uuid);
    }

    /**
     *  Zaak is nog niet geladen, beschikbare zaak-data uit de taak vast weergeven totdat de zaak is geladen
     */
    private createZaakFromTaak(taak: Taak): void {
        const zaak = new Zaak();
        zaak.identificatie = taak.zaakIdentificatie;
        zaak.uuid = taak.zaakUuid;
        zaak.zaaktype = new Zaaktype();
        zaak.zaaktype.omschrijving = taak.zaaktypeOmschrijving;
        this.zaak = zaak;
    }
}
