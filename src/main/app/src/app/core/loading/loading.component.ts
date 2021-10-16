/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {UtilService} from '../service/util.service';

@Component({
    selector: 'zac-loading',
    templateUrl: './loading.component.html',
    styleUrls: ['./loading.component.less']
})
export class LoadingComponent implements OnInit {

    loading: boolean;

    constructor(private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.utilService.loading$.subscribe(value => this.loading = value);
    }
}
