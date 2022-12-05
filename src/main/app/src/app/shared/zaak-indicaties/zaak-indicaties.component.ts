/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {Zaak} from '../../zaken/model/zaak';
import {ZaakRelatietype} from '../../zaken/model/zaak-relatietype';
import {GerelateerdeZaak} from '../../zaken/model/gerelateerde-zaak';
import {Indicatie} from '../informatie-object-indicaties/model/indicatie';
import {TranslateService} from '@ngx-translate/core';

export enum ZaakIndicatieType {
    OPSCHORTING = 'OPSCHORTING',
    HEROPEND = 'HEROPEND',
    HOOFDZAAK = 'HOOFDZAAK',
    DEELZAAK = 'DEELZAAK',
    VERLENGD = 'VERLENGD'
}

@Component({
    selector: 'zac-zaak-indicaties',
    templateUrl: './zaak-indicaties.component.html',
    styleUrls: ['./zaak-indicaties.component.less']
})
export class ZaakIndicatiesComponent implements OnInit {

    indicaties: Indicatie[];

    @Input() zaakZoekObject: ZaakZoekObject;
    @Input() zaak: Zaak;

    constructor(private translateService: TranslateService) {}

    ngOnInit(): void {
        if (this.zaakZoekObject) {
            this.indicaties = [
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.OPSCHORTING,
                    visible: this.zaakZoekObject.indicatieOpschorting,
                    primary: true,
                    tooltipSuffix: {list: this.zaakZoekObject.redenOpschorting}
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HEROPEND,
                    visible: this.zaakZoekObject.indicatieHeropend,
                    primary: true,
                    tooltipSuffix: {list: this.zaakZoekObject.statusToelichting}
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HOOFDZAAK,
                    visible: this.zaakZoekObject.indicatieHoofdzaak,
                    contentOverride: {list: 'HZ'}
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.DEELZAAK,
                    visible: this.zaakZoekObject.indicatieDeelzaak,
                    contentOverride: {list: 'DZ'}
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.VERLENGD,
                    visible: this.zaakZoekObject.indicatieVerlenging,
                    tooltipSuffix: {list: this.zaakZoekObject.redenVerlenging}
                })
            ]
        } else if (this.zaak) {
            this.indicaties = [
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.OPSCHORTING,
                    visible: this.zaak.isOpgeschort,
                    primary: true
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HEROPEND,
                    visible: this.zaak.isHeropend,
                    primary: true
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HOOFDZAAK,
                    visible: this.zaak.isHoofdzaak,
                    tooltipOverride: {view: this.translateService.instant(this.getDeelZaakToelichting(), this.getDeelZaakArgs())},
                    contentOverride: {list: 'HZ'}
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.DEELZAAK,
                    visible: this.zaak.isDeelzaak,
                    tooltipOverride: {view: this.translateService.instant('msg.zaak.relatie', {'identificatie': this.getHoofdzaakID()})},
                    contentOverride: {list: 'DZ'}
                }),
                new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.VERLENGD,
                    visible: this.zaak.isVerlengd,
                    tooltipSuffix: {list: this.zaakZoekObject.redenVerlenging}
                })
            ]
        } else {
            throw Error('zaakZoekObject or zaak must be set');
        }
    }

    getHoofdzaakID(): string {
        return this.zaak.gerelateerdeZaken.find(gerelateerdeZaak => gerelateerdeZaak.relatieType === ZaakRelatietype.HOOFDZAAK).identificatie;
    }

    getDeelzaken(): GerelateerdeZaak[] {
        return this.zaak.gerelateerdeZaken.filter(gerelateerdeZaak => gerelateerdeZaak.relatieType === ZaakRelatietype.DEELZAAK);
    }

    getDeelZaakToelichting(): string {
        return this.getDeelzaken().length === 1 ? 'msg.zaak.relatie' : 'msg.zaak.relaties';
    }

    getDeelZaakArgs(): object {
        const deelzaken = this.getDeelzaken();
        return deelzaken.length === 1 ? {identificatie: deelzaken[0].identificatie} : {aantal: deelzaken.length};
    }
}
