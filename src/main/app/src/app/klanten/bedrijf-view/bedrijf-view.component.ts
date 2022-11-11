/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Bedrijf} from '../model/bedrijven/bedrijf';
import {Observable, share} from 'rxjs';
import {KlantenService} from '../klanten.service';
import {SkeletonLayout} from '../../shared/skeleton-loader/skeleton-loader-options';

@Component({
    templateUrl: './bedrijf-view.component.html',
    styleUrls: ['./bedrijf-view.component.less']
})
export class BedrijfViewComponent implements OnInit {

    bedrijfIdentificatie: string;
    vestigingsnummer: string;
    rsin: string;
    bedrijf: Bedrijf;
    bedrijf$: Observable<Bedrijf>;
    skeletonLoaderLayout = SkeletonLayout;

    constructor(private utilService: UtilService, private _route: ActivatedRoute, private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        this._route.paramMap.subscribe((params: ParamMap) => {
            const vesOrRSIN: string = params.get('vesOrRSIN');
            this.bedrijfIdentificatie = vesOrRSIN;
            if (vesOrRSIN.length === 12) {
                this.vestigingsnummer = vesOrRSIN;
                this.rsin = null;
            } else {
                this.rsin = vesOrRSIN;
                this.vestigingsnummer = null;
            }
            this.loadBedrijf();
        });
        this.utilService.setTitle('bedrijfsgegevens');
    }

    private loadBedrijf(): void {
        if (this.vestigingsnummer) {
            this.loadVestiging();
        } else {
            if (this.rsin) {
                this.loadRechtspersoon();
            }
        }
    }

    private loadVestiging(): void {
        this.bedrijf$ = this.klantenService.readVestiging(this.vestigingsnummer).pipe(share());
        this.bedrijf$.subscribe(bedrijf => {
            this.bedrijf = bedrijf;
        });
    }

    private loadRechtspersoon(): void {
        this.bedrijf$ = this.klantenService.readRechtspersoon(this.rsin).pipe(share());
        this.bedrijf$.subscribe(bedrijf => {
            this.bedrijf = bedrijf;
        });
    }
}
