/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {Werklijst} from '../../gebruikersvoorkeuren/model/werklijst';
import {ZoekenDataSource} from '../../shared/dynamic-table/datasource/zoeken-data-source';
import {ZoekObjectType} from '../../zoeken/model/zoek-object-type';

/**
 * Datasource voor de werkvoorraad zaken. Via deze class wordt de data voor de tabel opgehaald
 */
export class ZakenWerkvoorraadDatasource extends ZoekenDataSource<ZaakZoekObject> {

    constructor(zoekenService: ZoekenService,
                utilService: UtilService) {
        super(Werklijst.WERKVOORRAAD_ZAKEN, zoekenService, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = ZoekObjectType.ZAAK;
        zoekParameters.alleenOpenstaandeZaken = true;
    }
}
