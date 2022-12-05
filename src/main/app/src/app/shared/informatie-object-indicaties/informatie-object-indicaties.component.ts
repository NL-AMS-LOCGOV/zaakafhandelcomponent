/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {DocumentZoekObject} from '../../zoeken/model/documenten/document-zoek-object';
import {TranslateService} from '@ngx-translate/core';
import {DatumPipe} from '../pipes/datum.pipe';
import {Indicatie} from './model/indicatie';

export enum InformatieObjectIndicatieType {
    VERGRENDELD = 'VERGRENDELD',
    GEBRUIKSRECHTEN = 'GEBRUIKSRECHTEN',
    ONDERTEKEND = 'ONDERTEKEND'
}

@Component({
    selector: 'zac-informatie-object-indicaties',
    templateUrl: './informatie-object-indicaties.component.html',
    styleUrls: ['./informatie-object-indicaties.component.less']
})
export class InformatieObjectIndicatiesComponent implements OnInit {

    indicaties: Indicatie[] = [];

    @Input() infoObject: EnkelvoudigInformatieobject;
    @Input() document: DocumentZoekObject;
    @Input() lijst: boolean;
    datumPipe = new DatumPipe('nl');

    constructor(private translateService: TranslateService) {}

    ngOnInit() {
        if (this.infoObject) {
            if (this.infoObject.gelockedDoor) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: InformatieObjectIndicatieType.VERGRENDELD,
                    primary: true,
                    tooltipOverride: {view: this.translateService.instant('msg.document.vergrendeld', {gebruiker: this.infoObject.gelockedDoor.naam})},
                    tooltipSuffix: {list: this.infoObject.gelockedDoor.naam}
                }));
            }
            if (this.infoObject.indicatieGebruiksrecht) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: InformatieObjectIndicatieType.GEBRUIKSRECHTEN,
                    primary: true
                }));
            }
            if (this.infoObject.ondertekening) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: InformatieObjectIndicatieType.ONDERTEKEND,
                    tooltipOverride: {view: this.infoObject.ondertekening.soort + '-' + this.datumPipe.transform(this.infoObject.ondertekening.datum)},
                    tooltipSuffix: {list: this.infoObject.ondertekening.soort + ' - ' + this.datumPipe.transform(this.infoObject.ondertekening.datum)}
                }));
            }
        } else if (this.document) {
            if (this.document.indicatieVergrendeld) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: InformatieObjectIndicatieType.VERGRENDELD,
                    primary: true,
                    tooltipOverride: {view: this.translateService.instant('msg.document.vergrendeld', {gebruiker: this.document.vergrendeldDoor})},
                    tooltipSuffix: {list: this.document.vergrendeldDoor}
                }));
            }
            if (this.document.indicatieGebruiksrecht) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: InformatieObjectIndicatieType.GEBRUIKSRECHTEN,
                    primary: true
                }));
            }
            if (this.document.indicatieOndertekend) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: InformatieObjectIndicatieType.ONDERTEKEND,
                    tooltipOverride: {view: this.document.ondertekeningSoort + '-' + this.datumPipe.transform(this.document.ondertekeningDatum)},
                    tooltipSuffix: {list: this.document.ondertekeningSoort + ' - ' + this.datumPipe.transform(this.document.ondertekeningDatum)}
                }));
            }
        } else {
            throw Error('infoObject or document must be set');
        }
    }
}
