/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {BreakpointObserver} from '@angular/cdk/layout';
import {UtilService} from '../core/service/util.service';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    nieuweZakenCard = {title: 'dashboard.mijn.nieuwe.zaken'};

    /** Based on the screen size, switch from standard to one column per row */
    cards = [
        [this.nieuweZakenCard]
    ];

    constructor(private breakpointObserver: BreakpointObserver, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.dashboard');
    }
}
