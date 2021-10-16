/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {map} from 'rxjs/operators';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Title} from '@angular/platform-browser';
import {UtilService} from '../core/service/util.service';

@Component({
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.less']
})
export class DashboardComponent implements OnInit {

    /** Based on the screen size, switch from standard to one column per row */
    cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
        map(({matches}) => {
            if (matches) {
                return [
                    {title: 'Card 1', cols: 1, rows: 1},
                    {title: 'Card 2', cols: 1, rows: 1},
                    {title: 'Card 3', cols: 1, rows: 1},
                    {title: 'Card 4', cols: 1, rows: 1}
                ];
            }

            return [
                {title: 'Card 1', cols: 2, rows: 1},
                {title: 'Card 2', cols: 1, rows: 1},
                {title: 'Card 3', cols: 1, rows: 2},
                {title: 'Card 4', cols: 1, rows: 1}
            ];
        })
    );

    constructor(private breakpointObserver: BreakpointObserver, private titleService: Title, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.titleService.setTitle('zaakafhandelcomponent - Dashboard');
        this.utilService.setHeaderTitle('Dashboard');
    }
}
