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

    indicaties: Indicatie[] = [];

    @Input() zaakZoekObject: ZaakZoekObject;
    @Input() zaak: Zaak;

    constructor(private translateService: TranslateService) {}

    ngOnInit(): void {
        if (this.zaakZoekObject) {
            if (this.zaakZoekObject.indicatieOpschorting) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.OPSCHORTING,
                    primary: true,
                    tooltipSuffix: {list: this.zaakZoekObject.redenOpschorting}
                }));
            }
            if (this.zaakZoekObject.indicatieHeropend) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HEROPEND,
                    primary: true,
                    tooltipSuffix: {list: this.zaakZoekObject.statusToelichting}
                }));
            }
            if (this.zaakZoekObject.indicatieHoofdzaak) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HOOFDZAAK,
                    contentOverride: {list: 'HZ'}
                }));
            }
            if (this.zaakZoekObject.indicatieDeelzaak) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.DEELZAAK,
                    contentOverride: {list: 'DZ'}
                }));
            }
            if (this.zaakZoekObject.indicatieVerlenging) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.VERLENGD,
                    tooltipSuffix: {list: this.zaakZoekObject.redenVerlenging}
                }));
            }
        } else if (this.zaak) {
            if (this.zaak.isOpgeschort) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.OPSCHORTING,
                    primary: true
                }));
            }
            if (this.zaak.isHeropend) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HEROPEND,
                    primary: true
                }));
            }
            if (this.zaak.isHoofdzaak) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.HOOFDZAAK,
                    tooltipOverride: {view: this.translateService.instant(this.getDeelZaakToelichting(), this.getDeelZaakArgs())},
                    contentOverride: {list: 'HZ'}
                }));
            }
            if (this.zaak.isDeelzaak) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.DEELZAAK,
                    tooltipOverride: {view: this.translateService.instant('msg.zaak.relatie', {'identificatie': this.getHoofdzaakID()})},
                    contentOverride: {list: 'DZ'}
                }));
            }
            if (this.zaak.isVerlengd) {
                this.indicaties.push(new Indicatie(this.translateService, {
                    type: ZaakIndicatieType.VERLENGD,
                    tooltipSuffix: {list: this.zaakZoekObject.redenVerlenging}
                }));
            }
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
