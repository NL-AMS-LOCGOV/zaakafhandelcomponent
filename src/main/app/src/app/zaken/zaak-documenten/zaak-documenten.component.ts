/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ScreenEvent} from '../../core/websocket/model/screen-event';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
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

@Component({
    selector: 'zac-zaak-documenten',
    templateUrl: './zaak-documenten.component.html',
    styleUrls: ['./zaak-documenten.component.less'],
    animations: [detailExpand]
})
export class ZaakDocumentenComponent implements OnInit, AfterViewInit, OnDestroy {
    @Input() zaak: Zaak;
    @Input() zaakUUID: string;

    @Input() set documentToegevoegd(informatieobject: EnkelvoudigInformatieobject) {
        if (informatieobject) {
            this.enkelvoudigInformatieObjecten.data = [...this.enkelvoudigInformatieObjecten.data, informatieobject];
        }
    }

    taakModus: boolean;

    @ViewChild('documentenTable', {read: MatSort, static: true}) docSort: MatSort;

    informatieObjecten$: Observable<EnkelvoudigInformatieobject[]>;

    enkelvoudigInformatieObjecten: MatTableDataSource<EnkelvoudigInformatieobject> = new MatTableDataSource<EnkelvoudigInformatieobject>();
    documentPreviewRow: EnkelvoudigInformatieobject | null;

    private zaakDocumentenListener: WebsocketListener;

    constructor(private informatieObjectenService: InformatieObjectenService,
                private websocketService: WebsocketService,
                private utilService: UtilService,
                private zakenService: ZakenService,
                private dialog: MatDialog,
                private translate: TranslateService) { }

    ngOnInit(): void {
        this.taakModus = !!this.zaakUUID;

        this.zaakDocumentenListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_INFORMATIEOBJECTEN,
            this.taakModus ? this.zaakUUID : this.zaak.uuid,
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
            console.log('callback loadInformatieObjecten: ' + event.key);
        }
        const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
        zoekParameters.zaakUUID = this.taakModus ? this.zaakUUID : this.zaak.uuid;

        this.informatieObjecten$ = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters).pipe(share());

        this.informatieObjecten$.subscribe(objecten => {
            this.enkelvoudigInformatieObjecten.data = objecten;
        });
    }

    documentVerplaatsen(informatieobject: EnkelvoudigInformatieobject): void {
        if (!this.taakModus) {
            this.informatieObjectenService.addTeVerplaatsenDocument(informatieobject, this.zaak.identificatie);
        }
    }

    documentOntkoppelen(informatieobject: EnkelvoudigInformatieobject): void {
        if (!this.taakModus) {
            informatieobject['loading'] = true;
            this.utilService.setLoading(true);
            this.zakenService.findZakenForInformatieobject(informatieobject.uuid).pipe(
                map(zaakIDs => {
                    delete informatieobject['loading'];
                    this.utilService.setLoading(false);
                    return zaakIDs.filter(zaakID => zaakID !== this.zaak.identificatie).join(', ');
                })
            ).subscribe(zaakIDs => {
                let melding: string;
                if (zaakIDs) {
                    melding = this.translate.instant('actie.document.ontkoppelen.meerdere.zaken.bevestigen',
                        {zaken: zaakIDs, document: informatieobject.titel});
                } else {
                    melding = this.translate.instant('actie.document.ontkoppelen.bevestigen', {document: informatieobject.titel});
                }
                const dialogData = new DialogData([
                        new TextareaFormFieldBuilder().id('reden')
                                                      .label('reden')
                                                      .build()],
                    (results: any[]) => this.zakenService.ontkoppelInformatieObject(this.zaak.uuid, informatieobject.uuid, results['reden']),
                    melding);
                this.dialog.open(DialogComponent, {
                    data: dialogData
                }).afterClosed().subscribe(result => {
                    if (result) {
                        this.utilService.openSnackbar('actie.document.ontkoppelen.uitgevoerd', {document: informatieobject.titel});
                        this.websocketService.suspendListener(this.zaakDocumentenListener);
                        this.loadInformatieObjecten();
                    }
                });
            });
        }
    }

    isDocumentVerplaatsenDisabled(informatieobject: EnkelvoudigInformatieobject): boolean {
        return this.informatieObjectenService.isReedsTeVerplaatsen(informatieobject);
    }

    isOntkoppelenDisabled(informatieobject: EnkelvoudigInformatieobject): boolean {
        return informatieobject['loading'] || this.informatieObjectenService.isReedsTeVerplaatsen(informatieobject);
    }

    isPreviewBeschikbaar(formaat: FileFormat): boolean {
        return FileFormatUtil.isPreviewAvailable(formaat);
    }

}
