/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';

import {Besluit} from '../model/besluit';
import {ZakenService} from '../zaken.service';
import {shareReplay} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';

@Component({
    selector: 'zac-besluit-view',
    templateUrl: './besluit-view.component.html',
    styleUrls: ['./besluit-view.component.less']
})
export class BesluitViewComponent implements OnInit {
    @Input() besluit: Besluit;

    besluitInformatieobjectenList$: Observable<EnkelvoudigInformatieobject[]>;

    constructor(private zakenService: ZakenService) {
    }

    ngOnInit(): void {
        this.besluitInformatieobjectenList$ = this.zakenService.listBesluitInformatieobjecten(encodeURIComponent(this.besluit.url)).pipe(
            shareReplay()
        );
    }
}
