/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Bedrijf} from '../model/bedrijven/bedrijf';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {Observable, share} from 'rxjs';
import {SkeletonLayout} from '../../shared/skeleton-loader/skeleton-loader-options';

@Component({
    selector: 'zac-bedrijfsgegevens',
    templateUrl: './bedrijfsgegevens.component.html'
})
export class BedrijfsgegevensComponent implements OnInit, AfterViewInit {
    @Input() isVerwijderbaar: boolean;
    @Output() delete = new EventEmitter<Bedrijf>();

    private _vestigingsnummer: string;
    @Input() set vestigingsnummer(identificatie: string) {
        this._vestigingsnummer = identificatie;
        this.loadVestiging();
    }

    get vestigingsnummer(): string {
        return this._vestigingsnummer;
    }

    private _rsin: string;
    @Input() set rsin(identificatie: string) {
        this._rsin = identificatie;
        this.loadRechtspersoon();
    }

    get rsin(): string {
        return this._rsin;
    }

    skeletonLayout = SkeletonLayout;
    bedrijf: Bedrijf;
    bedrijf$: Observable<Bedrijf>;
    klantExpanded: boolean;
    viewInitialized = false;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.klantExpanded = SessionStorageUtil.getItem('klantExpanded', true);
        this.loadBedrijf();
    }

    private loadBedrijf(): void {
        if (this._vestigingsnummer) {
            this.loadVestiging();
        } else {
            if (this._rsin) {
                this.loadRechtspersoon();
            }
        }
    }

    private loadVestiging(): void {
        this.bedrijf$ = this.klantenService.readVestiging(this._vestigingsnummer).pipe(share());
        this.bedrijf$.subscribe(bedrijf => {
            this.bedrijf = bedrijf;
        });
    }

    private loadRechtspersoon(): void {
        this.bedrijf$ = this.klantenService.readRechtspersoon(this._rsin).pipe(share());
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
