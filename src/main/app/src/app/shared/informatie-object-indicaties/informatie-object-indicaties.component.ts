/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {DocumentZoekObject} from '../../zoeken/model/documenten/document-zoek-object';
import {TranslateService} from '@ngx-translate/core';

export enum InformatieObjectIndicatieType {
    VERGRENDELD = 'VERGRENDELD',
    GEBRUIKSRECHTEN = 'GEBRUIKSRECHTEN',
    ONDERTEKEND = 'ONDERTEKEND'
}

export interface InformatieObjectIndicatie {
    visible: boolean,
    tooltipSuffix?: string,
    args?: {}
}

@Component({
    selector: 'zac-informatie-object-indicaties',
    templateUrl: './informatie-object-indicaties.component.html',
    styleUrls: ['./informatie-object-indicaties.component.less']
})
export class InformatieObjectIndicatiesComponent implements OnInit {

    private readonly INDICATIE_PREFIX: string = 'indicatie';

    indicaties: { [key in InformatieObjectIndicatieType]: InformatieObjectIndicatie };

    @Input() infoObject: EnkelvoudigInformatieobject;
    @Input() document: DocumentZoekObject;
    @Input() lijst: boolean;

    constructor(private translateService: TranslateService) {}

    ngOnInit() {
        if (this.infoObject != undefined) {
            this.indicaties = {
                VERGRENDELD: {
                    visible: !!this.infoObject.gelockedDoor,
                    tooltipSuffix: this.infoObject.gelockedDoor.naam,
                    args: this.lijst ? null : {gebruiker: this.infoObject.gelockedDoor.naam}
                },
                GEBRUIKSRECHTEN: {
                    visible: this.infoObject.indicatieGebruiksrecht
                },
                ONDERTEKEND: {
                    visible: !!this.infoObject.ondertekening,
                    tooltipSuffix: this.infoObject.ondertekening.soort + '-' + this.infoObject.ondertekening.datum //format date!
                }
            };
        } else if (this.document != undefined) {
            this.indicaties = {
                VERGRENDELD: {
                    visible: this.document.indicatieVergrendeld,
                    tooltipSuffix: this.document.vergrendeldDoor,
                    args: this.lijst ? null : {gebruiker: this.document.vergrendeldDoor}
                },
                GEBRUIKSRECHTEN: {
                    visible: this.document.indicatieGebruiksrecht
                },
                ONDERTEKEND: {
                    visible: this.document.indicatieOndertekend,
                    tooltipSuffix: this.document.ondertekeningSoort + '-' + this.document.ondertekeningDatum //format date!
                }
            };
        } else {
            throw new Error('Either infoObject or document is required');
        }
    }

    getTooltipFor(type: keyof typeof InformatieObjectIndicatieType): string {
        const tooltip = this.translateService.instant(`${this.INDICATIE_PREFIX}.${type}`, this.indicaties[type].args);
        if (this.indicaties[type].tooltipSuffix) {
            return `${tooltip}: ${this.indicaties[type].tooltipSuffix}`;
        }
        return tooltip;
    }
}
