/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {Bedrijf} from '../model/bedrijven/bedrijf';

@Component({
    selector: 'zac-bedrijfsgegevens',
    templateUrl: './bedrijfsgegevens.component.html',
    styleUrls: ['./bedrijfsgegevens.component.less']
})
export class BedrijfsgegevensComponent implements OnChanges {
    @Input() isVerwijderbaar: boolean;
    @Input() isWijzigbaar: boolean;
    @Input() rsinOfVestigingsnummer: string;
    @Output() delete = new EventEmitter<Bedrijf>();
    @Output() edit = new EventEmitter<Bedrijf>();

    bedrijf: Bedrijf;
    klantExpanded: boolean;

    constructor(private klantenService: KlantenService) {
    }

    ngOnChanges(): void {
        this.bedrijf = null;
        if (this.rsinOfVestigingsnummer) {
            this.klantenService.readBedrijf(this.rsinOfVestigingsnummer).subscribe(bedrijf => {
                this.bedrijf = bedrijf;
                this.klantExpanded = true;
            });
        }
    }
}
