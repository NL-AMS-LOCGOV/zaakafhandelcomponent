/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {ZaakOverzicht} from '../../zaken/model/zaak-overzicht';
import {SignaleringenService} from '../../signaleringen.service';
import {DashboardCardComponent} from '../dashboard-card/dashboard-card.component';

@Component({
    selector: 'zac-zaken-card',
    templateUrl: './zaken-card.component.html',
    styleUrls: ['../dashboard-card/dashboard-card.component.less', './zaken-card.component.less']
})
export class ZakenCardComponent extends DashboardCardComponent<ZaakOverzicht> implements OnInit {

    columns: string[] = ['identificatie', 'startdatum', 'zaaktype', 'omschrijving', 'url'];

    constructor(private signaleringenService: SignaleringenService) {
        super();
    }

    ngOnInit(): void {
        this.signaleringenService.listZakenSignalering(this.data.signaleringType).subscribe(zaken => {
            this.dataSource.data = zaken;
        });
    }

}
