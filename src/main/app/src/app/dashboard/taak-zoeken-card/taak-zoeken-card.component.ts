/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {DashboardCardComponent} from '../dashboard-card/dashboard-card.component';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ZoekObject} from '../../zoeken/model/zoek-object';
import {TakenMijnDatasource} from '../../taken/taken-mijn/taken-mijn-datasource';
import {SorteerVeld} from '../../zoeken/model/sorteer-veld';

@Component({
    selector: 'zac-taak-zoeken-card',
    templateUrl: './taak-zoeken-card.component.html',
    styleUrls: ['../dashboard-card/dashboard-card.component.less', './taak-zoeken-card.component.less']
})
export class TaakZoekenCardComponent extends DashboardCardComponent<ZoekObject> implements OnInit {

    columns: string[] = ['naam', 'creatiedatum', 'zaakIdentificatie', 'zaaktypeOmschrijving', 'url'];

    constructor(private zoekenService: ZoekenService) {
        super();
    }

    ngOnInit(): void {
        const zoekParameters: ZoekParameters = TakenMijnDatasource.mijnLopendeTaken(new ZoekParameters());
        zoekParameters.sorteerVeld = SorteerVeld.TAAK_STREEFDATUM;
        zoekParameters.sorteerRichting = 'asc';
        this.zoekenService.list(zoekParameters).subscribe(zoekResultaat => {
            this.dataSource.data = zoekResultaat.resultaten;
        });
    }
}
