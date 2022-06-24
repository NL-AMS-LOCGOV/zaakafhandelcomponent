/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {OntkoppeldDocument} from '../model/ontkoppeld-document';
import {OntkoppeldeDocumentenService} from '../ontkoppelde-documenten.service';
import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {merge} from 'rxjs';
import {map, startWith, switchMap} from 'rxjs/operators';
import {ListParameters} from '../../shared/model/list-parameters';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {MatTableDataSource} from '@angular/material/table';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../shared/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectVerplaatsService} from '../../informatie-objecten/informatie-object-verplaats.service';

@Component({
    templateUrl: './ontkoppelde-documenten-list.component.html',
    styleUrls: ['./ontkoppelde-documenten-list.component.less']
})
export class OntkoppeldeDocumentenListComponent implements OnInit, AfterViewInit {

    isLoadingResults = true;
    dataSource: MatTableDataSource<OntkoppeldDocument> = new MatTableDataSource<OntkoppeldDocument>();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    displayedColumns: string[] = ['titel', 'creatiedatum', 'zaakID', 'ontkoppeldDoor', 'ontkoppeldOp', 'reden', 'actions'];
    defaults: ListParameters;

    constructor(private service: OntkoppeldeDocumentenService,
                private infoService: InformatieObjectenService,
                private utilService: UtilService,
                public dialog: MatDialog,
                private translate: TranslateService,
                private informatieObjectVerplaatsService: InformatieObjectVerplaatsService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.documenten.ontkoppeldeDocumenten');
        this.defaults = SessionStorageUtil.getItem('ontkoppeldeDocumenten', new ListParameters('ontkoppeldOp', 'desc')) as ListParameters;
    }

    ngAfterViewInit(): void {
        this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
        merge(this.sort.sortChange, this.paginator.page).pipe(
            startWith({}),
            switchMap(() => {
                this.isLoadingResults = true;
                this.utilService.setLoading(true);
                return this.service.list(this.getListParameters());
            }),
            map(data => {
                this.isLoadingResults = false;
                this.utilService.setLoading(false);
                SessionStorageUtil.setItem('ontkoppeldeDocumenten', this.getListParameters());
                return data;
            })
        ).subscribe(data => {
            this.paginator.length = data.totaal;
            this.dataSource.data = data.resultaten;
        });
    }

    getListParameters(): ListParameters {
        const params = new ListParameters(this.sort.active, this.sort.direction);
        params.page = this.paginator.pageIndex;
        params.maxResults = this.paginator.pageSize;
        return params;
    }

    getDownloadURL(od: OntkoppeldDocument): string {
        return this.infoService.getDownloadURL(od.documentUUID);
    }

    reset(): void {
        SessionStorageUtil.setItem('ontkoppeldeDocumenten', new ListParameters('documentID', 'desc'));
        this.utilService.reloadRoute();
    }

    documentVerplaatsen(od: OntkoppeldDocument): void {
        this.informatieObjectVerplaatsService.addTeVerplaatsenDocument(OntkoppeldeDocumentenListComponent.getInformatieobject(od), 'ontkoppelde-documenten');
    }

    documentVerwijderen(od: OntkoppeldDocument): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('msg.document.verwijderen.bevestigen', {document: od.titel}),
                this.service.delete(od)
            )
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('msg.document.verwijderen.uitgevoerd', {document: od.titel});
                this.paginator.page.emit();
            }
        });
    }

    isDocumentVerplaatsenDisabled(od: OntkoppeldDocument): boolean {
        return this.informatieObjectVerplaatsService.isReedsTeVerplaatsen(OntkoppeldeDocumentenListComponent.getInformatieobject(od));
    }

    private static getInformatieobject(ontkoppeldDocument: OntkoppeldDocument): EnkelvoudigInformatieobject {
        const informatieobject = new EnkelvoudigInformatieobject();
        informatieobject.titel = ontkoppeldDocument.titel;
        informatieobject.uuid = ontkoppeldDocument.documentUUID;
        informatieobject.identificatie = ontkoppeldDocument.documentID;
        return informatieobject;
    }

}
