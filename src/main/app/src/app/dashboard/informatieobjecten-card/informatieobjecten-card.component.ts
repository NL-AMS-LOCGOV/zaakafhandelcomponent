/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {SignaleringenService} from '../../signaleringen.service';
import {DashboardCardComponent} from '../dashboard-card/dashboard-card.component';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';

@Component({
    selector: 'zac-informatieobjecten-card',
    templateUrl: './informatieobjecten-card.component.html',
    styleUrls: ['../dashboard-card/dashboard-card.component.less', './informatieobjecten-card.component.less']
})
export class InformatieobjectenCardComponent extends DashboardCardComponent<EnkelvoudigInformatieobject> implements OnInit {

    columns: string[] = ['titel', 'registratiedatumTijd', 'informatieobjectType', 'auteur', 'url'];

    constructor(private signaleringenService: SignaleringenService) {
        super();
    }

    ngOnInit(): void {
        this.signaleringenService.listInformatieobjectenSignalering(this.data.signaleringType).subscribe(informatieobjecten => {
            this.dataSource.data = informatieobjecten;
        });
    }

}
