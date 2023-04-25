/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Bedrijf} from '../model/bedrijven/bedrijf';

@Component({
    templateUrl: './bedrijf-view.component.html',
    styleUrls: ['./bedrijf-view.component.less']
})
export class BedrijfViewComponent implements OnInit {

    bedrijf: Bedrijf;

    constructor(private utilService: UtilService, private _route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('bedrijfsgegevens');
        this._route.data.subscribe(data => {
            this.bedrijf = data.bedrijf;
        });
    }
}
