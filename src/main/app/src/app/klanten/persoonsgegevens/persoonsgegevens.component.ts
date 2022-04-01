/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Persoon} from '../model/personen/persoon';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';

@Component({
    selector: 'zac-persoongegevens',
    templateUrl: './persoonsgegevens.component.html'
})
export class PersoonsgegevensComponent implements OnInit, AfterViewInit {

    @Input() bsn: string;
    @Output() delete = new EventEmitter<Persoon>();

    persoon: Persoon;
    klantExpanded: boolean;
    viewInitialized = false;
    loading = true;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.klantExpanded = SessionStorageUtil.getItem('klantExpanded', true);
        this.klantenService.readPersoon(this.bsn).subscribe(persoon => {
            this.persoon = persoon;
            this.loading = false;
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
