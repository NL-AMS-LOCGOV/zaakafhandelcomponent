/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {AfterViewInit, Component, EventEmitter, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {merge} from 'rxjs';
import {map, startWith, switchMap} from 'rxjs/operators';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatDialog} from '@angular/material/dialog';
import {Werklijst} from '../../gebruikersvoorkeuren/model/werklijst';
import {Zoekopdracht} from '../../gebruikersvoorkeuren/model/zoekopdracht';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {WerklijstComponent} from '../../shared/dynamic-table/datasource/werklijst-component';
import {GebruikersvoorkeurenService} from '../../gebruikersvoorkeuren/gebruikersvoorkeuren.service';
import {ActivatedRoute} from '@angular/router';
import {ProductaanvragenService} from '../productaanvragen.service';
import {InboxProductaanvraag} from '../model/inbox-productaanvraag';
import {InboxProductaanvraagListParameters} from '../model/inbox-productaanvraag-list-parameters';

@Component({
    templateUrl: './inbox-productaanvragen-list.component.html',
    styleUrls: ['./inbox-productaanvragen-list.component.less']
})
export class InboxProductaanvragenListComponent extends WerklijstComponent implements OnInit, AfterViewInit {

    isLoadingResults = true;
    dataSource: MatTableDataSource<InboxProductaanvraag> = new MatTableDataSource<InboxProductaanvraag>();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    displayedColumns: string[] = ['type', 'ontvangstdatum', 'initiator', 'actions'];
    filterColumns: string[] = ['type_filter', 'ontvangstdatum_filter', 'initiator_filter', 'actions_filter'];
    listParameters: InboxProductaanvraagListParameters;
    filterType: string[] = [];
    filterChange: EventEmitter<void> = new EventEmitter<void>();
    clearZoekopdracht: EventEmitter<void> = new EventEmitter<void>();

    constructor(private productaanvragenService: ProductaanvragenService,
                private infoService: InformatieObjectenService,
                private utilService: UtilService,
                public dialog: MatDialog,
                public gebruikersvoorkeurenService: GebruikersvoorkeurenService,
                public route: ActivatedRoute) {
        super();
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.utilService.setTitle('title.productaanvragen.inboxProductaanvragen');
        this.listParameters = SessionStorageUtil.getItem(Werklijst.INBOX_PRODUCTAANVRAGEN + '_ZOEKPARAMETERS', this.createDefaultParameters());
    }

    ngAfterViewInit(): void {
        this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
        merge(this.sort.sortChange, this.paginator.page, this.filterChange).pipe(
            startWith({}),
            switchMap(() => {
                this.isLoadingResults = true;
                this.utilService.setLoading(true);
                this.updateListParameters();
                return this.productaanvragenService.list(this.listParameters);
            }),
            map(data => {
                this.isLoadingResults = false;
                this.utilService.setLoading(false);
                return data;
            })
        ).subscribe(data => {
            this.paginator.length = data.totaal;
            this.filterType = data.filterType;
            this.dataSource.data = data.resultaten;
        });
    }

    updateListParameters(): void {
        this.listParameters.sort = this.sort.active;
        this.listParameters.order = this.sort.direction;
        this.listParameters.page = this.paginator.pageIndex;
        this.listParameters.maxResults = this.paginator.pageSize;
        SessionStorageUtil.setItem(Werklijst.INBOX_PRODUCTAANVRAGEN + '_ZOEKPARAMETERS', this.listParameters);
    }

    getDownloadURL(ip: InboxProductaanvraag): string {
        return this.infoService.getDownloadURL(ip.aanvraagdocumentUUID);
    }

    filtersChanged(): void {
        this.paginator.pageIndex = 0;
        this.clearZoekopdracht.emit();
        this.filterChange.emit();
    }

    resetSearch(): void {
        this.listParameters = SessionStorageUtil.setItem(Werklijst.INBOX_PRODUCTAANVRAGEN + '_ZOEKPARAMETERS', this.createDefaultParameters());
        this.sort.active = this.listParameters.sort;
        this.sort.direction = this.listParameters.order;
        this.paginator.pageIndex = 0;
        this.filterChange.emit();
    }

    zoekopdrachtChanged(actieveZoekopdracht: Zoekopdracht): void {
        if (actieveZoekopdracht) {
            this.listParameters = JSON.parse(actieveZoekopdracht.json);
            this.sort.active = this.listParameters.sort;
            this.sort.direction = this.listParameters.order;
            this.paginator.pageIndex = 0;
            this.filterChange.emit();
        } else if (actieveZoekopdracht === null) {
            this.resetSearch();
        } else {
            this.filterChange.emit();
        }
    }

    createDefaultParameters(): InboxProductaanvraagListParameters {
        return new InboxProductaanvraagListParameters('ontvangstdatum', 'desc');
    }

    getWerklijst(): Werklijst {
        return Werklijst.INBOX_PRODUCTAANVRAGEN;
    }

}
