/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Input, OnInit} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../../informatie-objecten/model/enkelvoudig-informatieobject';
import {DocumentZoekObject} from '../../../zoeken/model/documenten/document-zoek-object';
import {TranslateService} from '@ngx-translate/core';
import {DatumPipe} from '../../pipes/datum.pipe';
import {Indicatie} from '../../model/indicatie';
import {IndicatiesComponent} from '../indicaties.component';

export enum InformatieobjectIndicatie {
    VERGRENDELD = 'VERGRENDELD',
    GEBRUIKSRECHT = 'GEBRUIKSRECHT',
    ONDERTEKEND = 'ONDERTEKEND'
}

@Component({
    selector: 'zac-informatie-object-indicaties',
    templateUrl: '../indicaties.component.html',
    styleUrls: ['../indicaties.component.less']
})
export class InformatieObjectIndicatiesComponent extends IndicatiesComponent implements OnInit {
    datumPipe = new DatumPipe('nl');

    @Input() document: EnkelvoudigInformatieobject;
    @Input() documentZoekObject: DocumentZoekObject;

    constructor(private translateService: TranslateService) {
        super();
    }

    ngOnInit() {
        const indicaties = this.documentZoekObject ? this.documentZoekObject.indicaties : this.document.indicaties;
        indicaties.forEach(indicatie => {
            switch (indicatie) {
                case InformatieobjectIndicatie.VERGRENDELD:
                    this.indicaties.push(new Indicatie(indicatie, true, this.getVergrendeldToelichting()));
                    break;
                case InformatieobjectIndicatie.ONDERTEKEND:
                    this.indicaties.push(new Indicatie(indicatie, false, this.getOndertekeningToelichting()));
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
}
