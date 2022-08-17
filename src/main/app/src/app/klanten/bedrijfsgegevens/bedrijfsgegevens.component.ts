/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Bedrijf} from '../model/bedrijven/bedrijf';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {Observable, share} from 'rxjs';

@Component({
    selector: 'zac-bedrijfsgegevens',
    templateUrl: './bedrijfsgegevens.component.html'
})
export class BedrijfsgegevensComponent implements OnInit, AfterViewInit {

    @Input() vestigingsnummer;
    @Input() rsin;
    @Input() isVerwijderbaar: boolean;
    @Output() delete = new EventEmitter<Bedrijf>();

    bedrijf: Bedrijf;
    bedrijf$: Observable<Bedrijf>;
    klantExpanded: boolean;
    viewInitialized = false;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.klantExpanded = SessionStorageUtil.getItem('klantExpanded', true);
        if (this.vestigingsnummer) {
            this.bedrijf$ = this.klantenService.readVestiging(this.vestigingsnummer).pipe(share());
        }
        if (this.rsin) {
            this.bedrijf$ = this.klantenService.readRechtspersoon(this.rsin).pipe(share());
        }
        this.bedrijf$.subscribe(bedrijf => {
            this.bedrijf = bedrijf;
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
