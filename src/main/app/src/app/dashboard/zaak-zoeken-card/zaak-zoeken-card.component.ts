/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {DashboardCardComponent} from '../dashboard-card/dashboard-card.component';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ZoekObject} from '../../zoeken/model/zoek-object';
import {ZakenMijnDatasource} from '../../zaken/zaken-mijn/zaken-mijn-datasource';
import {SorteerVeld} from '../../zoeken/model/sorteer-veld';

@Component({
    selector: 'zac-zaak-zoeken-card',
    templateUrl: './zaak-zoeken-card.component.html',
    styleUrls: ['../dashboard-card/dashboard-card.component.less', './zaak-zoeken-card.component.less']
})
export class ZaakZoekenCardComponent extends DashboardCardComponent<ZoekObject> implements OnInit {

    columns: string[] = ['identificatie', 'startdatum', 'zaaktypeOmschrijving', 'omschrijving', 'url'];

    constructor(private zoekenService: ZoekenService) {
        super();
    }

    ngOnInit(): void {
        const zoekParameters: ZoekParameters = ZakenMijnDatasource.mijnLopendeZaken(new ZoekParameters());
        zoekParameters.sorteerVeld = SorteerVeld.ZAAK_EINDDATUM_GEPLAND;
        zoekParameters.sorteerRichting = 'asc';
        this.zoekenService.list(zoekParameters).subscribe(zoekResultaat => {
            this.dataSource.data = zoekResultaat.resultaten;
        });
    }
}
