/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input} from '@angular/core';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {Zaak} from '../model/zaak';
import {ZaakRelatietype} from '../model/zaak-relatietype';
import {GerelateerdeZaak} from '../model/gerelateerde-zaak';

@Component({
    selector: 'zac-zaak-indicaties',
    templateUrl: './zaak-indicaties.component.html',
    styleUrls: ['./zaak-indicaties.component.less']
})
export class ZaakIndicatiesComponent {
    @Input() zaakZoekObject: ZaakZoekObject;
    @Input() zaak: Zaak;

    constructor() {}

    getHoofdzaakID(): string {
        return this.zaak.gerelateerdeZaken.find(gerelateerdeZaak => gerelateerdeZaak.relatieType === ZaakRelatietype.HOOFDZAAK).identificatie;
    }

    getDeelzaken(): GerelateerdeZaak[] {
        return this.zaak.gerelateerdeZaken.filter(gerelateerdeZaak => gerelateerdeZaak.relatieType === ZaakRelatietype.DEELZAAK);
    }

    getDeelZaakToelichting(): string {
        return this.getDeelzaken().length === 1 ? 'indicatie.relatie.toelichting' : 'indicatie.relatie.toelichting.meerdere.zaken';
    }

    getDeelZaakArgs(): object {
        const deelzaken = this.getDeelzaken();
        return deelzaken.length === 1 ? {identificatie: deelzaken[0].identificatie} : {aantal: deelzaken.length};
    }
}
