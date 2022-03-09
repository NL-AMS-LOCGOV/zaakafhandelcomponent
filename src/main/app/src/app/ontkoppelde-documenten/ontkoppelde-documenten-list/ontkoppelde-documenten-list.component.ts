/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {SessionStorageService} from '../../shared/storage/session-storage.service';
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

@Component({
    templateUrl: './ontkoppelde-documenten-list.component.html',
    styleUrls: ['./ontkoppelde-documenten-list.component.less']
})
export class OntkoppeldeDocumentenListComponent implements OnInit, AfterViewInit {

    isLoadingResults = true;
    dataSource: MatTableDataSource<OntkoppeldDocument> = new MatTableDataSource<OntkoppeldDocument>();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    displayedColumns: string[] = ['documentID', 'creatiedatum', 'titel', 'actions'];
    defaults: ListParameters;

    constructor(private service: OntkoppeldeDocumentenService,
                private infoService: InformatieObjectenService,
                private utilService: UtilService,
                private sessionStorageService: SessionStorageService) { }

    ngOnInit(): void {
        this.utilService.setTitle('title.documenten.ontkoppeldeDocumenten');
        this.defaults = this.sessionStorageService.getSessionStorage('ontkoppeldeDocumenten', new ListParameters('documentID', 'desc')) as ListParameters;
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
                this.sessionStorageService.setSessionStorage('ontkoppeldeDocumenten', this.getListParameters());
                return data;
            })
        ).subscribe(data => {
            this.paginator.length = data.count;
            this.dataSource.data = data.results;
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
        this.sessionStorageService.setSessionStorage('ontkoppeldeDocumenten', new ListParameters('documentID', 'desc'));
        this.utilService.reloadRoute();
    }
}
