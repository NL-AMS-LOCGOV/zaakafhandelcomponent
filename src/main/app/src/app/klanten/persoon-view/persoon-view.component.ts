/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Persoon} from '../model/personen/persoon';
import {KlantenService} from '../klanten.service';
import {Observable, share} from 'rxjs';

@Component({
    templateUrl: './persoon-view.component.html',
    styleUrls: ['./persoon-view.component.less']
})
export class PersoonViewComponent implements OnInit {

    bsn: string = null;
    persoon: Persoon;
    persoon$: Observable<Persoon>;

    constructor(private utilService: UtilService, private _route: ActivatedRoute, private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.utilService.setTitle('persoonsgegevens');
        this._route.paramMap.subscribe((params: ParamMap) => {
            this.bsn = params.get('bsn');
            this.loadPersoon();
        });
    }

    private loadPersoon(): void {
        this.persoon$ = this.klantenService.readPersoon(this.bsn).pipe(share());
        this.persoon$.subscribe(persoon => {
            this.persoon = persoon;
        });
    }
}
