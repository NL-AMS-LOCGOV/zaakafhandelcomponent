/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {OntkoppeldDocument} from '../model/ontkoppeld-document';
import {OntkoppeldeDocumentenService} from '../ontkoppelde-documenten.service';
import {AfterViewInit, Component, EventEmitter, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {merge} from 'rxjs';
import {map, startWith, switchMap} from 'rxjs/operators';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {MatTableDataSource} from '@angular/material/table';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../shared/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectVerplaatsService} from '../../informatie-objecten/informatie-object-verplaats.service';
import {OntkoppeldDocumentListParameters} from '../model/ontkoppeld-document-list-parameters';
import {User} from '../../identity/model/user';

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
    filterColumns: string[] = [
        'titel_filter', 'creatiedatum_filter', 'zaakID_filter', 'ontkoppeldDoor_filter', 'ontkoppeldOp_filter', 'reden_filter', 'actions_filter'
    ];
    parameters: OntkoppeldDocumentListParameters;
    filterOntkoppeldDoor: User[] = [];
    filterChange: EventEmitter<void> = new EventEmitter<void>();

    constructor(private service: OntkoppeldeDocumentenService,
                private infoService: InformatieObjectenService,
                private utilService: UtilService,
                public dialog: MatDialog,
                private translate: TranslateService,
                private informatieObjectVerplaatsService: InformatieObjectVerplaatsService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.documenten.ontkoppeldeDocumenten');
        this.parameters = SessionStorageUtil.getItem('ontkoppeldeDocumenten', this.createDefaultParameters());
    }

    ngAfterViewInit(): void {
        this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
        merge(this.sort.sortChange, this.paginator.page, this.filterChange).pipe(
            startWith({}),
            switchMap(() => {
                this.isLoadingResults = true;
                this.utilService.setLoading(true);
                this.updateListParameters();
                return this.service.list(this.parameters);
            }),
            map(data => {
                this.isLoadingResults = false;
                this.utilService.setLoading(false);
                SessionStorageUtil.setItem('ontkoppeldeDocumenten', this.parameters);
                return data;
            })
        ).subscribe(data => {
            this.paginator.length = data.totaal;
            this.filterOntkoppeldDoor = data.filterOntkoppeldDoor;
            this.dataSource.data = data.resultaten;
        });
    }

    updateListParameters(): void {
        this.parameters.sort = this.sort.active;
        this.parameters.order = this.sort.direction;
        this.parameters.page = this.paginator.pageIndex;
        this.parameters.maxResults = this.paginator.pageSize;
    }

    getDownloadURL(od: OntkoppeldDocument): string {
        return this.infoService.getDownloadURL(od.documentUUID);
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

    filtersChanged(): void {
        this.paginator.pageIndex = 0;
        this.filterChange.emit();
    }

    resetSearch(): void {
        this.parameters = this.createDefaultParameters();
        this.filtersChanged();
    }

    createDefaultParameters(): OntkoppeldDocumentListParameters {
        return new OntkoppeldDocumentListParameters('ontkoppeldOp', 'desc');
    }

    compareUser = (user1: User, user2: User): boolean => {
        return user1?.id === user2?.id;
    };
}
