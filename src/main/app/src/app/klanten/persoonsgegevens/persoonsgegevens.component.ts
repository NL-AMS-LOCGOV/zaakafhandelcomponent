/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Persoon} from '../model/personen/persoon';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {Observable, share} from 'rxjs';

@Component({
    selector: 'zac-persoongegevens',
    templateUrl: './persoonsgegevens.component.html'
})
export class PersoonsgegevensComponent implements OnInit, AfterViewInit {

    @Input() bsn: string;
    @Input() isWijzigbaar: boolean;
    @Output() delete = new EventEmitter<Persoon>();

    persoon: Persoon;
    persoon$: Observable<Persoon>;
    klantExpanded: boolean;
    viewInitialized = false;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.persoon$ = this.klantenService.readPersoon(this.bsn).pipe(share());
        this.klantExpanded = SessionStorageUtil.getItem('klantExpanded', true);

        this.persoon$.subscribe(persoon => {
            this.persoon = persoon;
        });
    }

    klantExpandedChanged($event: boolean): void {
        if (this.viewInitialized) {
            SessionStorageUtil.setItem('klantExpanded', $event ? 'true' : 'false');
        }
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
    }
}
