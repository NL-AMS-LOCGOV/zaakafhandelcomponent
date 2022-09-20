/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {ZaakOverzicht} from '../../zaken/model/zaak-overzicht';
import {DashboardCardComponent} from '../dashboard-card/dashboard-card.component';
import {ZakenService} from '../../zaken/zaken.service';
import {Conditionals} from '../../shared/edit/conditional-fn';

@Component({
    selector: 'zac-zaak-waarschuwingen-card',
    templateUrl: './zaak-waarschuwingen-card.component.html',
    styleUrls: ['../dashboard-card/dashboard-card.component.less', './zaak-waarschuwingen-card.component.less']
})
export class ZaakWaarschuwingenCardComponent extends DashboardCardComponent<ZaakOverzicht> implements OnInit {

    columns: string[] = ['identificatie', 'streefdatum', 'dagenTotStreefdatum', 'fataledatum', 'dagenTotFataledatum', 'url'];

    constructor(private zakenService: ZakenService) {
        super();
    }

    isAfterDate(datum, actual): boolean {
        return Conditionals.isOverschreden(datum, actual);
    }

    ngOnInit(): void {
        this.zakenService.listZaakWaarschuwingen().subscribe(zaken => {
            this.dataSource.data = zaken;
        });
    }

}
