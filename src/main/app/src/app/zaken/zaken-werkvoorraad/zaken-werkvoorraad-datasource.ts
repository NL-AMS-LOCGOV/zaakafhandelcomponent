/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';

/**
 * Datasource voor de werkvoorraad zaken. Via deze class wordt de data voor de tabel opgehaald
 */
export class ZakenWerkvoorraadDatasource extends ZoekenTableDataSource<ZaakZoekObject> {

    constructor(zoekenService: ZoekenService, utilService: UtilService) {
        super('zakenWerkvoorraad', zoekenService, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.alleenOpenstaandeZaken = true;
    }
}
