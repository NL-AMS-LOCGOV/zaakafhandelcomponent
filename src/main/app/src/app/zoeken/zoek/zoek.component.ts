/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, ViewChild} from '@angular/core';

import {ZoekObject, ZoekObjectType} from '../model/zoek-object';
import {ZoekenService} from '../zoeken.service';
import {ZoekParameters} from '../model/zoek-parameters';
import {Resultaat} from '../../shared/model/resultaat';
import {MatPaginator} from '@angular/material/paginator';
import {merge} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {UtilService} from '../../core/service/util.service';
import {MatSidenav} from '@angular/material/sidenav';
import {ZaakZoekObject} from '../model/zaken/zaak-zoek-object';
import {FormControl} from '@angular/forms';
import {ZoekVeld} from '../model/zoek-veld';
import {TaakZoekObject} from '../model/taken/taak-zoek-object';

@Component({
    selector: 'zac-zoeken',
    templateUrl: './zoek.component.html',
    styleUrls: ['./zoek.component.less']
})
export class ZoekComponent implements AfterViewInit {

    @ViewChild('paginator') paginator: MatPaginator;
    @Input() sideNav: MatSidenav;
    zoekObjectType = ZoekObjectType;
    zoekResultaat: Resultaat<ZoekObject> = {totaal: 0, foutmelding: '', resultaten: []};
    isLoadingResults = true;
    slow = false;
    zoekenControl = new FormControl('');
    zoek = new EventEmitter<void>();
    once = false;

    constructor(private zoekService: ZoekenService, public utilService: UtilService) {
    }

    ngAfterViewInit(): void {
        this.sideNav.openedChange.subscribe(() => {
            if (!this.once) {
                this.once = true;
                this.zoek.emit();
            }
        });

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
        const zoekParameters: ZoekParameters = new ZoekParameters();
        zoekParameters.zoeken[ZoekVeld.ALLE] = this.zoekenControl.value;
        zoekParameters.page = this.paginator.pageIndex;
        zoekParameters.rows = this.paginator.pageSize;
        return zoekParameters;
    }

    getZaakZoekObject(zoekObject: ZoekObject): ZaakZoekObject {
        return zoekObject as ZaakZoekObject;
    }

    getTaakZoekObject(zoekObject: ZoekObject): TaakZoekObject {
        return zoekObject as TaakZoekObject;
    }
}
