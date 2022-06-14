/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {InboxDocumentenService} from '../inbox-documenten.service';
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
import {InboxDocument} from '../model/inbox-document';

@Component({
    templateUrl: './inbox-documenten-list.component.html',
    styleUrls: ['./inbox-documenten-list.component.less']
})
export class InboxDocumentenListComponent implements OnInit, AfterViewInit {

    isLoadingResults = true;
    dataSource: MatTableDataSource<InboxDocument> = new MatTableDataSource<InboxDocument>();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    displayedColumns: string[] = ['enkelvoudiginformatieobjectID', 'creatiedatum', 'titel', 'actions'];
    defaults: ListParameters;

    constructor(private inboxDocumentenService: InboxDocumentenService,
                private infoService: InformatieObjectenService,
                private utilService: UtilService,
                public dialog: MatDialog,
                private translate: TranslateService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.documenten.inboxDocumenten');
        this.defaults = SessionStorageUtil.getItem('inboxDocumenten', new ListParameters('creatiedatum', 'desc')) as ListParameters;
    }

    ngAfterViewInit(): void {
        this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
        merge(this.sort.sortChange, this.paginator.page).pipe(
            startWith({}),
            switchMap(() => {
                this.isLoadingResults = true;
                this.utilService.setLoading(true);
                return this.inboxDocumentenService.list(this.getListParameters());
            }),
            map(data => {
                this.isLoadingResults = false;
                this.utilService.setLoading(false);
                SessionStorageUtil.setItem('inboxDocumenten', this.getListParameters());
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

    getDownloadURL(od: InboxDocument): string {
        return this.infoService.getDownloadURL(od.enkelvoudiginformatieobjectUUID);
    }

    reset(): void {
        SessionStorageUtil.setItem('inboxDocumenten', new ListParameters('creatiedatum', 'desc'));
        this.utilService.reloadRoute();
    }

    documentVerplaatsen(od: InboxDocument): void {
        this.infoService.addTeVerplaatsenDocument(InboxDocumentenListComponent.getInformatieobject(od), 'inbox-documenten');
    }

    documentVerwijderen(inboxDocument: InboxDocument): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('msg.document.verwijderen.bevestigen', {document: inboxDocument.titel}),
                this.inboxDocumentenService.delete(inboxDocument)
            )
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('msg.document.verwijderen.uitgevoerd', {document: inboxDocument.titel});
                this.paginator.page.emit();
            }
        });
    }

    isDocumentVerplaatsenDisabled(inboxDocument: InboxDocument): boolean {
        return this.infoService.isReedsTeVerplaatsen(InboxDocumentenListComponent.getInformatieobject(inboxDocument));
    }

    private static getInformatieobject(inboxDocument: InboxDocument): EnkelvoudigInformatieobject {
        const informatieobject = new EnkelvoudigInformatieobject();
        informatieobject.titel = inboxDocument.titel;
        informatieobject.uuid = inboxDocument.enkelvoudiginformatieobjectUUID;
        informatieobject.identificatie = inboxDocument.enkelvoudiginformatieobjectID;
        return informatieobject;
    }

}
