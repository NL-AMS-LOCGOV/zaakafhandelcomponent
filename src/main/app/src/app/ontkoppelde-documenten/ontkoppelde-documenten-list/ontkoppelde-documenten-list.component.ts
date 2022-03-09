/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ActivatedRoute} from '@angular/router';

import {UtilService} from '../../core/service/util.service';
import {SessionStorageService} from '../../shared/storage/session-storage.service';
import {OntkoppeldDocument} from '../model/ontkoppeld-document';
import {OntkoppeldeDocumentenService} from '../ontkoppelde-documenten.service';
import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {merge} from 'rxjs';
import {map, startWith, switchMap} from 'rxjs/operators';
import {ListParameters} from '../../shared/model/list-parameters';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';

@Component({
    templateUrl: './ontkoppelde-documenten-list.component.html',
    styleUrls: ['./ontkoppelde-documenten-list.component.less']
})
export class OntkoppeldeDocumentenListComponent implements AfterViewInit {

    isLoadingResults = true;
    data: OntkoppeldDocument[] = [];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    displayedColumns: string[] = ['documentID', 'creatiedatum', 'titel', 'actions'];

    constructor(private route: ActivatedRoute, private service: OntkoppeldeDocumentenService,
                private infoService: InformatieObjectenService, public utilService: UtilService,
                private sessionStorageService: SessionStorageService) { }

    ngAfterViewInit(): void {
        this.setDefaults();
        this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
        merge(this.sort.sortChange, this.paginator.page)
        .pipe(
            startWith({}),
            switchMap(() => {
                this.isLoadingResults = true;
                return this.service.list(this.getListParameters());
            }),
            map(data => {
                this.isLoadingResults = false;
                this.sessionStorageService.setSessionStorage('ontkoppelde-documenten', this.getListParameters());
                data.forEach(od => {
                    od['viewLink'] = `/informatie-objecten/${od.documentUUID}`;
                    od['downloadLink'] = this.infoService.getDownloadURL(od.documentUUID);
                });
                return data;
            })
        )
        .subscribe(data => (this.data = data));
    }

    getListParameters(): ListParameters {
        const params = new ListParameters();
        params.sort = this.sort.active;
        params.order = this.sort.direction;
        params.page = this.paginator.pageIndex;
        params.maxResults = this.paginator.pageSize;
        return params;
    }

    setDefaults(): void {
        const data = this.sessionStorageService.getSessionStorage('ontkoppelde-documenten', new ListParameters()) as ListParameters;
        this.paginator.pageIndex = data.page;
        this.paginator.pageSize = data.maxResults;
        if (data.sort) {
            this.sort.active = data.sort;
            this.sort.direction = data.order;
        }
    }
}
