/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {merge, Observable} from 'rxjs';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {MatTableDataSource} from '@angular/material/table';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ZoekVeld} from '../../zoeken/model/zoek-veld';
import {ZoekObjectType} from '../../zoeken/model/zoek-object';
import {ZoekResultaat} from '../../zoeken/model/zoek-resultaat';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap} from 'rxjs/operators';
import {SorteerVeld} from '../../zoeken/model/sorteer-veld';
import {FilterVeld} from '../../zoeken/model/filter-veld';
import {DatumVeld} from '../../zoeken/model/datum-veld';

@Component({
    selector: 'zac-klant-zaken-tabel',
    templateUrl: './klant-zaken-tabel.component.html',
    styleUrls: ['./klant-zaken-tabel.component.less']
})
export class KlantZakenTabelComponent implements OnInit, AfterViewInit, OnChanges {
    @Input() klantIdentificatie: string;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    dataSource: MatTableDataSource<ZaakZoekObject> = new MatTableDataSource<ZaakZoekObject>();
    columns: string[] = ['identificatie', 'status', 'startdatum', 'zaaktype', 'omschrijving', 'url'];
    filerColumns: string[] = this.columns.map(n => n + '_filter');
    isLoadingResults = true;
    sorteerVeld = SorteerVeld;
    zoekVeld = ZoekVeld;
    filterVeld = FilterVeld;
    datumVeld = DatumVeld;
    filterChange: EventEmitter<void> = new EventEmitter<void>();
    zoekParameters = new ZoekParameters();
    beschikbareFilters: { [key: string]: string[] } = {};
    init: boolean;

    constructor(private utilService: UtilService, private zoekenService: ZoekenService) {}

    ngOnInit(): void {
        this.zoekParameters.type = ZoekObjectType.ZAAK;
        this.zoekParameters.zoeken[ZoekVeld.ZAAK_INITIATOR] = this.klantIdentificatie;
    }

    private loadZaken(): Observable<ZoekResultaat<ZaakZoekObject>> {
        this.zoekParameters.page = this.paginator.pageIndex;
        this.zoekParameters.sorteerRichting = this.sort.direction;
        this.zoekParameters.sorteerVeld = this.sort.active;
        this.zoekParameters.rows = this.paginator.pageSize;
        return this.zoekenService.list(this.zoekParameters) as Observable<ZoekResultaat<ZaakZoekObject>>;
    }

    ngAfterViewInit(): void {
        this.init = true;
        this.filtersChanged();
        this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
        merge(this.sort.sortChange, this.paginator.page, this.filterChange).pipe(
            startWith({}),
            switchMap(() => {
                this.isLoadingResults = true;
                this.utilService.setLoading(true);
                return this.loadZaken();
            }),
            map(data => {
                this.isLoadingResults = false;
                this.utilService.setLoading(false);
                return data;
            })
        ).subscribe(data => {
            this.beschikbareFilters = data.filters;
            this.paginator.length = data.totaal;
            this.dataSource.data = data.resultaten;
        });
    }

    filtersChanged(): void {
        this.paginator.pageIndex = 0;
        this.filterChange.emit();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.klantIdentificatie = changes.klantIdentificatie.currentValue;
        if (this.init) {
            this.filtersChanged();
        }
    }
}
