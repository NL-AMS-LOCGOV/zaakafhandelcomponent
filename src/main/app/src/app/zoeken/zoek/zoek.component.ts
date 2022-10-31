/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, ViewChild} from '@angular/core';

import {ZoekObject, ZoekObjectType} from '../model/zoek-object';
import {ZoekenService} from '../zoeken.service';
import {ZoekParameters} from '../model/zoek-parameters';
import {MatPaginator} from '@angular/material/paginator';
import {merge} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav} from '@angular/material/sidenav';
import {ZaakZoekObject} from '../model/zaken/zaak-zoek-object';
import {FormControl} from '@angular/forms';
import {ZoekVeld} from '../model/zoek-veld';
import {TaakZoekObject} from '../model/taken/taak-zoek-object';
import {ZoekResultaat} from '../model/zoek-resultaat';

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

    readonly zoekObjectType = ZoekObjectType;
    zoekResultaat: ZoekResultaat<ZoekObject> = {totaal: 0, foutmelding: '', resultaten: [], filters: null};
    zoekParameters: ZoekParameters = new ZoekParameters();
    isLoadingResults = true;
    slow = false;
    zoekenControl = new FormControl('');
    zoek = new EventEmitter<void>();

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
            this.zoekResultaat = data;
        });

    }

    getZoekParameters(): ZoekParameters {
        this.zoekParameters.zoeken[ZoekVeld.ALLE] = this.zoekenControl.value;
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

    hasOption(options: string[]) {
        return options.length ? !(options.length === 1 && options[0] === '-NULL-') : false;
    }

    originalOrder = () => 0;
}
