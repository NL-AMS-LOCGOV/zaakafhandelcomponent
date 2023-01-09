/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {ZaakZoekObject} from '../../../zoeken/model/zaken/zaak-zoek-object';
import {Zaak} from '../../../zaken/model/zaak';
import {ZaakRelatietype} from '../../../zaken/model/zaak-relatietype';
import {Indicatie} from '../../model/indicatie';
import {TranslateService} from '@ngx-translate/core';
import {IndicatiesComponent} from '../indicaties.component';
import {WebsocketListener} from '../../../core/websocket/model/websocket-listener';
import {Opcode} from '../../../core/websocket/model/opcode';
import {ObjectType} from '../../../core/websocket/model/object-type';
import {WebsocketService} from '../../../core/websocket/websocket.service';
import {ScreenEvent} from '../../../core/websocket/model/screen-event';
import {ZakenService} from '../../../zaken/zaken.service';

export enum ZaakIndicatie {
    OPSCHORTING = 'OPSCHORTING',
    HEROPEND = 'HEROPEND',
    HOOFDZAAK = 'HOOFDZAAK',
    DEELZAAK = 'DEELZAAK',
    VERLENGD = 'VERLENGD'
}

@Component({
    selector: 'zac-zaak-indicaties',
    templateUrl: '../indicaties.component.html',
    styleUrls: ['../indicaties.component.less']
})
export class ZaakIndicatiesComponent extends IndicatiesComponent implements OnInit {
    @Input() zaakZoekObject: ZaakZoekObject;
    @Input() zaak: Zaak;

    private zaakListener: WebsocketListener;

    constructor(private translateService: TranslateService,
                private websocketService: WebsocketService,
                private zakenService: ZakenService) {
        super();
    }

    ngOnInit(): void {
        this.zaakListener = this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK, this.zaak.uuid,
            (event) => {
                this.loadZaak(event);
            });

        this.loadIndicaties();
    }

    loadIndicaties(): void {
        const indicaties = this.zaak ? this.zaak.indicaties : this.zaakZoekObject.indicaties;
        indicaties.forEach(indicatie => {
            switch (indicatie) {
                case ZaakIndicatie.OPSCHORTING:
                    this.indicaties.push(new Indicatie(indicatie, true, this.getRedenOpschorting()));
                    break;
                case ZaakIndicatie.HEROPEND:
                    this.indicaties.push(new Indicatie(indicatie, true, this.getStatusToelichting()));
                    break;
                case ZaakIndicatie.HOOFDZAAK:
                    this.indicaties.push(new Indicatie(indicatie, false, this.getHoofdzaakToelichting()));
                    break;
                case ZaakIndicatie.DEELZAAK:
                    this.indicaties.push(new Indicatie(indicatie, false, this.getDeelZaakToelichting()));
                    break;
                case ZaakIndicatie.VERLENGD:
                    this.indicaties.push(new Indicatie(indicatie, false, this.getRedenVerlenging()));
                    break;
            }
        });
    }

    private getRedenOpschorting(): string {
        return this.zaakZoekObject ? this.zaakZoekObject.redenOpschorting : this.zaak.redenOpschorting;
    }

    private getStatusToelichting(): string {
        return this.zaakZoekObject ? this.zaakZoekObject.statusToelichting : this.zaak.status.toelichting;
    }

    private getDeelZaakToelichting(): string {
        if (this.zaak) {
            console.log(this.zaak.gerelateerdeZaken);
            const hoofdzaakID = this.zaak.gerelateerdeZaken.find(gerelateerdeZaak => gerelateerdeZaak.relatieType === ZaakRelatietype.HOOFDZAAK).identificatie;
            return this.translateService.instant('msg.zaak.relatie', {identificatie: hoofdzaakID});
        }
        return '';
    }

    private getHoofdzaakToelichting(): string {
        if (this.zaak) {
            const deelzaken = this.zaak.gerelateerdeZaken.filter(gerelateerdeZaak => gerelateerdeZaak.relatieType === ZaakRelatietype.DEELZAAK);
            const toelichting = deelzaken.length === 1 ? 'msg.zaak.relatie' : 'msg.zaak.relaties';
            const args = deelzaken.length === 1 ? {identificatie: deelzaken[0].identificatie} : {aantal: deelzaken.length};
            return this.translateService.instant(toelichting, args);
        }
        return '';
    }

    private getRedenVerlenging() {
        return this.zaakZoekObject ? this.zaakZoekObject.redenVerlenging : this.zaak.redenVerlenging;
    }

    private loadZaak(event?: ScreenEvent) {
        if (event) {
            console.debug('callback loadZaak: ' + event.key);
        }
        this.zakenService.readZaak(this.zaak.uuid).subscribe(zaak => {
            this.zaak = zaak;
            this.loadIndicaties();
        });
    }
}
