/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {DocumentZoekObject} from '../../../zoeken/model/documenten/document-zoek-object';
import {TranslateService} from '@ngx-translate/core';
import {DatumPipe} from '../../pipes/datum.pipe';
import {Indicatie} from '../../model/indicatie';
import {IndicatiesComponent} from '../indicaties.component';
import {Opcode} from '../../../core/websocket/model/opcode';
import {ObjectType} from '../../../core/websocket/model/object-type';
import {WebsocketListener} from '../../../core/websocket/model/websocket-listener';
import {WebsocketService} from '../../../core/websocket/websocket.service';
import {ScreenEvent} from '../../../core/websocket/model/screen-event';
import {InformatieObjectenService} from '../../../informatie-objecten/informatie-objecten.service';

export enum InformatieobjectIndicatie {
    VERGRENDELD = 'VERGRENDELD',
    ONDERTEKEND = 'ONDERTEKEND',
    BESLUIT = 'BESLUIT',
    GEBRUIKSRECHT = 'GEBRUIKSRECHT',
}

@Component({
    selector: 'zac-informatie-object-indicaties',
    templateUrl: '../indicaties.component.html',
    styleUrls: ['../indicaties.component.less']
})
export class InformatieObjectIndicatiesComponent extends IndicatiesComponent implements OnInit, OnDestroy {
    datumPipe = new DatumPipe('nl');

    @Input() document: EnkelvoudigInformatieobject;
    @Input() documentZoekObject: DocumentZoekObject;

    private documentListener: WebsocketListener;

    constructor(private translateService: TranslateService,
                private informatieObjectenService: InformatieObjectenService,
                private websocketService: WebsocketService) {
        super();
    }

    ngOnInit() {
        this.documentListener = this.websocketService.addListener(Opcode.UPDATED,
            ObjectType.ENKELVOUDIG_INFORMATIEOBJECT, this.document.uuid,
            (event) => {
                this.loadInformatieObject(event);
            });

        this.loadIndicaties();
    }

    ngOnDestroy() {
        this.websocketService.removeListener(this.documentListener);
    }

    private loadIndicaties(): void {
        this.indicaties = [];
        const indicaties = this.documentZoekObject ? this.documentZoekObject.indicaties : this.document.indicaties;
        indicaties.forEach(indicatie => {
            switch (indicatie) {
                case InformatieobjectIndicatie.VERGRENDELD:
                    this.indicaties.push(new Indicatie(indicatie, true, this.getVergrendeldToelichting()));
                    break;
                case InformatieobjectIndicatie.ONDERTEKEND:
                    this.indicaties.push(new Indicatie(indicatie, false, this.getOndertekeningToelichting()));
                    break;
                case InformatieobjectIndicatie.BESLUIT:
                    this.indicaties.push(new Indicatie(indicatie, false, this.translateService.instant('msg.document.besluit')));
                    break;
                case InformatieobjectIndicatie.GEBRUIKSRECHT:
                    this.indicaties.push(new Indicatie(indicatie, true, ''));
                    break;
            }
        });
    }

    private getOndertekeningToelichting(): string {
        if (this.documentZoekObject) {
            return this.documentZoekObject.ondertekeningSoort + '-' + this.datumPipe.transform(this.documentZoekObject.ondertekeningDatum);
        } else {
            return this.document.ondertekening.soort + '-' + this.datumPipe.transform(this.document.ondertekening.datum);
        }
    }

    private getVergrendeldToelichting(): string {
        if (this.documentZoekObject) {
            return this.translateService.instant('msg.document.vergrendeld', {gebruiker: this.documentZoekObject.vergrendeldDoor});
        } else {
            return this.translateService.instant('msg.document.vergrendeld', {gebruiker: this.document.gelockedDoor.naam});
        }
    }

    private loadInformatieObject(event?: ScreenEvent) {
        if (event) {
            console.debug('callback loadInformatieObject: ' + event.key);
        }
        this.informatieObjectenService.readEnkelvoudigInformatieobject(this.document.uuid).subscribe(infoObject => {
            this.document = infoObject;
            this.loadIndicaties();
        });
    }
}
