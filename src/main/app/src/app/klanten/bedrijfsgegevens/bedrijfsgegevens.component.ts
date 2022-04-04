/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Bedrijf} from '../model/bedrijven/bedrijf';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';

@Component({
    selector: 'zac-bedrijfsgegevens',
    templateUrl: './bedrijfsgegevens.component.html'
})
export class BedrijfsgegevensComponent implements OnInit, AfterViewInit {

    @Input() vestigingsnummer;
    @Output() delete = new EventEmitter<Bedrijf>();

    bedrijf: Bedrijf;
    klantExpanded: boolean;
    viewInitialized = false;
    loading = true;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.klantExpanded = SessionStorageUtil.getItem('klantExpanded', true);
        this.klantenService.readBedrijf(this.vestigingsnummer).subscribe(bedrijf => {
            this.bedrijf = bedrijf;
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
