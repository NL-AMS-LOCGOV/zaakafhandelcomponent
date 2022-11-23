/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, ViewChild} from '@angular/core';

import {ZoekObject} from '../model/zoek-object';
import {ZoekenService} from '../zoeken.service';
import {ZoekParameters} from '../model/zoek-parameters';
import {MatPaginator} from '@angular/material/paginator';
import {merge} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav} from '@angular/material/sidenav';
import {ZaakZoekObject} from '../model/zaken/zaak-zoek-object';
import {FormControl} from '@angular/forms';
import {TaakZoekObject} from '../model/taken/taak-zoek-object';
import {ZoekResultaat} from '../model/zoek-resultaat';
import {ZoekType} from '../model/zoek-type';
import {DocumentZoekObject} from '../model/documenten/document-zoek-object';
import {ZoekObjectType} from '../model/zoek-object-type';

@Component({
    selector: 'zac-zoeken',
    templateUrl: './zoek.component.html',
    styleUrls: ['./zoek.component.less']
})
export class ZoekComponent implements AfterViewInit {

    @ViewChild('paginator') paginator: MatPaginator;
    @Input() sideNav: MatSidenav;

    @Input() set keywords(value: string) {
        if (this.zoekenControl.value !== value) {
            this.zoekenControl.setValue(value);
            this.zoek.emit();
        }
    }

    get keywords(): string {
        return this.zoekenControl.value;
    }

    zoekType: ZoekType = ZoekType.ZAC;
    ZoekType = ZoekType;
    readonly zoekObjectType = ZoekObjectType;
    zoekResultaat: ZoekResultaat<ZoekObject> = new ZoekResultaat<ZoekObject>();
    zoekParameters: ZoekParameters = new ZoekParameters();
    isLoadingResults = true;
    slow = false;
    zoekenControl = new FormControl('');
    zoek = new EventEmitter<void>();
    hasSearched = false;
    hasTaken = false;
    hasZaken = false;
    hasDocument = false;

    constructor(private zoekService: ZoekenService, public utilService: UtilService) {
    }

    ngAfterViewInit(): void {
        this.zoek.subscribe(() => {
            this.paginator.pageIndex = 0;
        });
        merge(this.paginator.page, this.zoek).pipe(
            switchMap(() => {
                this.slow = false;
                setTimeout(() => {
                    this.slow = true;
                }, 500);
                this.isLoadingResults = true;
                this.utilService.setLoading(true);
                return this.zoekService.list(this.getZoekParameters());
            }),
            map(data => {
                this.isLoadingResults = false;
                this.utilService.setLoading(false);
                return data;
            })
        ).subscribe(data => {
            this.paginator.length = data.totaal;
            this.hasSearched = true;
            this.zoekResultaat = data;
            this.bepaalContext();
        });
    }

    bepaalContext(): void {
        this.hasZaken = this.zoekResultaat.filters.TYPE.find(f => f.naam === ZoekObjectType.ZAAK)?.aantal > 0;
        this.hasTaken = this.zoekResultaat.filters.TYPE.find(f => f.naam === ZoekObjectType.TAAK)?.aantal > 0;
        this.hasDocument = this.zoekResultaat.filters.TYPE.find(f => f.naam === ZoekObjectType.DOCUMENT)?.aantal > 0;
        if (this.zoekParameters.filters.TYPE?.length > 0) {
            if (this.hasZaken) {
                this.hasZaken = this.zoekParameters.filters.TYPE.includes(ZoekObjectType.ZAAK);
            }
            if (this.hasTaken) {
                this.hasTaken = this.zoekParameters.filters.TYPE.includes(ZoekObjectType.TAAK);
            }
            if (this.hasDocument) {
                this.hasDocument = this.zoekParameters.filters.TYPE.includes(ZoekObjectType.DOCUMENT);
            }
        }
    }

    getZoekParameters(): ZoekParameters {
        this.zoekParameters.zoeken.ALLE = this.zoekenControl.value;
        this.zoekParameters.page = this.paginator.pageIndex;
        this.zoekParameters.rows = this.paginator.pageSize;
        return this.zoekParameters;
    }

    getZaakZoekObject(zoekObject: ZoekObject): ZaakZoekObject {
        return zoekObject as ZaakZoekObject;
    }

    getTaakZoekObject(zoekObject: ZoekObject): TaakZoekObject {
        return zoekObject as TaakZoekObject;
    }

    getDocumentZoekObject(zoekObject: ZoekObject): DocumentZoekObject {
        return zoekObject as DocumentZoekObject;
    }

    hasOption(options: string[]) {
        return options.length ? !(options.length === 1 && options[0] === '-NULL-') : false;
    }

    keywordsChange() {
        if (this.zoekenControl.value !== this.zoekParameters.zoeken.ALLE) {
            this.zoek.emit();
        }
    }

    originalOrder = () => 0;

    setZoektype(zoekType: ZoekType): void {
        this.zoekType = zoekType;
        if (zoekType === ZoekType.ZAC) {
            this.zoekenControl.enable();
        } else {
            this.zoekenControl.disable();
        }
    }

}
