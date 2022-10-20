/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Persoon} from '../model/personen/persoon';
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';
import {Observable, share} from 'rxjs';
import {SkeletonLayout} from '../../shared/skeleton-loader/skeleton-loader-options';

@Component({
    selector: 'zac-persoongegevens',
    templateUrl: './persoonsgegevens.component.html'
})
export class PersoonsgegevensComponent implements OnInit, AfterViewInit {
    @Input() isVerwijderbaar: boolean;
    @Output() delete = new EventEmitter<Persoon>();

    private _bsn: string;
    @Input() set bsn(identificatie: string) {
        this._bsn = identificatie;
        this.loadPersoon();
    }

    get bsn(): string {
        return this._bsn;
    }

    readonly skeletonLayout = SkeletonLayout;
    persoon: Persoon;
    persoon$: Observable<Persoon>;
    klantExpanded: boolean;
    viewInitialized = false;

    constructor(private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this.klantExpanded = SessionStorageUtil.getItem('klantExpanded', true);
        this.loadPersoon();
    }

    private loadPersoon(): void {
        this.persoon$ = this.klantenService.readPersoon(this._bsn).pipe(share());
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
