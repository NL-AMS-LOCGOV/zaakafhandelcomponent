/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';

export class ZakenAfgehandeldDatasource extends ZoekenTableDataSource<ZaakZoekObject> {

    constructor(zoekenService: ZoekenService, utilService: UtilService) {
        super('zakenAfgehandeld', zoekenService, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = 'ZAAK';
        zoekParameters.alleenAfgeslotenZaken = true;
    }
}
