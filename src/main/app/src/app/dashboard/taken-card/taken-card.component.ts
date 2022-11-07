/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component} from '@angular/core';
import {SignaleringenService} from '../../signaleringen.service';
import {DashboardCardComponent} from '../dashboard-card/dashboard-card.component';
import {Taak} from '../../taken/model/taak';

@Component({
    selector: 'zac-taken-card',
    templateUrl: './taken-card.component.html',
    styleUrls: ['../dashboard-card/dashboard-card.component.less', './taken-card.component.less']
})
export class TakenCardComponent extends DashboardCardComponent<Taak> {

    columns: string[] = ['naam', 'creatiedatumTijd', 'zaakIdentificatie', 'zaaktypeOmschrijving', 'url'];

    constructor(private signaleringenService: SignaleringenService) {
        super();
    }

    protected onLoad(afterLoad: () => void): void {
        this.signaleringenService.listTakenSignalering(this.data.signaleringType).subscribe(taken => {
            this.dataSource.data = taken;
            afterLoad();
        });
    }
}
