/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {InformatieobjectZoekParameters} from '../../informatie-objecten/model/informatieobject-zoek-parameters';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {MatTableDataSource} from '@angular/material/table';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {FileFormat, FileFormatUtil} from '../../informatie-objecten/model/file-format';
import {Zaak} from '../model/zaak';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {WebsocketListener} from '../../core/websocket/model/websocket-listener';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {MatSort} from '@angular/material/sort';
import {map} from 'rxjs/operators';
import {DialogData} from '../../shared/dialog/dialog-data';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {DialogComponent} from '../../shared/dialog/dialog.component';
import {UtilService} from '../../core/service/util.service';
import {ZakenService} from '../zaken.service';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {detailExpand} from '../../shared/animations/animations';
import {Observable, share} from 'rxjs';
import {InformatieObjectVerplaatsService} from '../../informatie-objecten/informatie-object-verplaats.service';
import {GekoppeldeZaakEnkelvoudigInformatieobject} from '../../informatie-objecten/model/gekoppelde.zaak.enkelvoudig.informatieobject';
import {SelectionModel} from '@angular/cdk/collections';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {SkeletonLayout} from '../../shared/skeleton-loader/skeleton-loader-options';
import {IndicatiesLayout} from '../../shared/indicaties/indicaties.component';

@Component({
    selector: 'zac-zaak-documenten',
    templateUrl: './zaak-documenten.component.html',
    styleUrls: ['./zaak-documenten.component.less'],
    animations: [detailExpand]
})
export class ZaakDocumentenComponent implements OnInit, AfterViewInit, OnDestroy {
    readonly indicatiesLayout = IndicatiesLayout;
    @Input() zaak: Zaak;
    @Input() zaakUUID: string;

    taakModus: boolean;
    selectAll = false;
    toonGekoppeldeZaakDocumenten = false;
    documentColumns = ['downloaden', 'titel', 'informatieobjectTypeOmschrijving', 'status', 'vertrouwelijkheidaanduiding', 'creatiedatum', 'registratiedatumTijd', 'auteur', 'indicaties', 'url'];

    @ViewChild('documentenTable', {read: MatSort, static: true}) docSort: MatSort;

    informatieObjecten$: Observable<EnkelvoudigInformatieobject[]>;
    skeletonLayout = SkeletonLayout;

    enkelvoudigInformatieObjecten: MatTableDataSource<GekoppeldeZaakEnkelvoudigInformatieobject> =
        new MatTableDataSource<GekoppeldeZaakEnkelvoudigInformatieobject>();
    documentPreviewRow: EnkelvoudigInformatieobject | null;
    downloadAlsZipSelection = new SelectionModel<GekoppeldeZaakEnkelvoudigInformatieobject>(true, []);

    private zaakDocumentenListener: WebsocketListener;

    constructor(private informatieObjectenService: InformatieObjectenService,
                private websocketService: WebsocketService,
                private utilService: UtilService,
                private zakenService: ZakenService,
                private dialog: MatDialog,
                private translate: TranslateService,
                private informatieObjectVerplaatsService: InformatieObjectVerplaatsService,
                private router: Router) { }

    ngOnInit(): void {
        this.taakModus = !!this.zaakUUID;

        this.zaakDocumentenListener = this.websocketService.addListener(Opcode.UPDATED,
            ObjectType.ZAAK_INFORMATIEOBJECTEN, this.getZaakUuid(),
            (event) => this.loadInformatieObjecten(event));

        this.loadInformatieObjecten();
    }

    ngAfterViewInit(): void {
        this.enkelvoudigInformatieObjecten.sort = this.docSort;
    }

    ngOnDestroy(): void {
        this.websocketService.removeListener(this.zaakDocumentenListener);
    }

    private loadInformatieObjecten(event?: ScreenEvent): void {
        if (event) {
            console.debug('callback loadInformatieObjecten: ' + event.key);
            this.informatieObjectenService.readEnkelvoudigInformatieobjectByZaakInformatieobjectUUID(
                event.objectId.detail)
                .subscribe(enkelvoudigInformatieobject => {
                    this.utilService.openSnackbarAction(
                        'msg.document.toegevoegd.aan.zaak',
                        'actie.document.bekijken',
                        {document: enkelvoudigInformatieobject.titel},
                        7).subscribe(() => {
                        this.router.navigate(['/informatie-objecten', enkelvoudigInformatieobject.uuid]);
                    });
                });
        }
        const zoekParameters = new InformatieobjectZoekParameters();
        zoekParameters.zaakUUID = this.getZaakUuid();
        zoekParameters.gekoppeldeZaakDocumenten = this.toonGekoppeldeZaakDocumenten;

        this.informatieObjecten$ = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters)
                                       .pipe(share());

        this.informatieObjecten$.subscribe(objecten => {
            this.enkelvoudigInformatieObjecten.data = objecten;
        });
    }

    documentVerplaatsen(informatieobject: EnkelvoudigInformatieobject): void {
        if (!this.taakModus) {
            this.informatieObjectVerplaatsService.addTeVerplaatsenDocument(informatieobject, this.zaak.identificatie);
        }
    }

    documentOntkoppelen(informatieobject: EnkelvoudigInformatieobject): void {
        if (!this.taakModus) {
            informatieobject['loading'] = true;
            this.utilService.setLoading(true);
            this.informatieObjectenService.listZaakIdentificatiesForInformatieobject(informatieobject.uuid).pipe(
                map(zaakIDs => {
                    delete informatieobject['loading'];
                    this.utilService.setLoading(false);
                    return zaakIDs.filter(zaakID => zaakID !== this.zaak.identificatie).join(', ');
                })
            ).subscribe(zaakIDs => {
                let melding: string;
                if (zaakIDs) {
                    melding = this.translate.instant('msg.document.ontkoppelen.meerdere.zaken.bevestigen',
                        {zaken: zaakIDs, document: informatieobject.titel});
                } else {
                    melding = this.translate.instant('msg.document.ontkoppelen.bevestigen',
                        {document: informatieobject.titel});
                }
                const dialogData = new DialogData([
                        new TextareaFormFieldBuilder().id('reden')
                                                      .label('reden')
                                                      .validators(Validators.required)
                                                      .build()],
                    (results: any[]) => this.zakenService.ontkoppelInformatieObject(this.zaak.uuid,
                        informatieobject.uuid, results['reden']),
                    melding);
                this.dialog.open(DialogComponent, {
                    data: dialogData
                }).afterClosed().subscribe(result => {
                    if (result) {
                        this.utilService.openSnackbar('msg.document.ontkoppelen.uitgevoerd',
                            {document: informatieobject.titel});
                        this.websocketService.suspendListener(this.zaakDocumentenListener);
                        this.loadInformatieObjecten();
                    }
                });
            });
        }
    }

    isDocumentVerplaatsenDisabled(informatieobject: EnkelvoudigInformatieobject): boolean {
        return this.informatieObjectVerplaatsService.isReedsTeVerplaatsen(informatieobject);
    }

    isOntkoppelenDisabled(informatieobject: EnkelvoudigInformatieobject): boolean {
        return informatieobject['loading'] || this.informatieObjectVerplaatsService.isReedsTeVerplaatsen(
            informatieobject);
    }

    isPreviewBeschikbaar(formaat: FileFormat): boolean {
        return FileFormatUtil.isPreviewAvailable(formaat);
    }

    toggleGekoppeldeZaakDocumenten() {
        this.documentColumns = this.toonGekoppeldeZaakDocumenten ?
            ['downloaden', 'titel', 'zaakIdentificatie', 'relatieType', 'informatieobjectTypeOmschrijving', 'status', 'vertrouwelijkheidaanduiding', 'creatiedatum', 'registratiedatumTijd', 'auteur', 'indicaties', 'url'] :
            ['downloaden', 'titel', 'relatieType', 'informatieobjectTypeOmschrijving', 'status', 'vertrouwelijkheidaanduiding', 'creatiedatum', 'registratiedatumTijd', 'auteur', 'indicaties', 'url'];
        this.loadInformatieObjecten();
    }

    getZaakUuid(): string {
        return this.taakModus ? this.zaakUUID : this.zaak.uuid;
    }

    getZaakUuidVanInformatieObject(informatieObject: GekoppeldeZaakEnkelvoudigInformatieobject): string {
        return informatieObject.zaakUUID ? informatieObject.zaakUUID : this.taakModus ? this.zaakUUID : this.zaak.uuid;
    }

    updateSelected($event: MatCheckboxChange, document): void {
        if ($event) {
            this.downloadAlsZipSelection.toggle(document);
        }
    }

    downloadAlsZip() {
        const uuids: string[] = [];
        this.downloadAlsZipSelection.selected.forEach(document => {
            uuids.push(document.uuid);
        });

        this.downloadAlsZipSelection.clear();
        this.selectAll = false;

        return this.informatieObjectenService.getZIPDownload(uuids).subscribe(response => {
            this.utilService.downloadBlobResponse(response, this.zaak.identificatie);
        });
    }

    updateAll($event: MatCheckboxChange) {
        this.selectAll = !this.selectAll;
        if ($event) {
            this.enkelvoudigInformatieObjecten.data.forEach(document => {
                $event.checked ? this.downloadAlsZipSelection.select(document) :
                    this.downloadAlsZipSelection.deselect(document);
            });
        }
    }
}
