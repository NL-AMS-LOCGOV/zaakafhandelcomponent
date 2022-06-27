/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';

export class TakenMijnDatasource extends ZoekenTableDataSource<TaakZoekObject> {

    constructor(zoekenService: ZoekenService, utilService: UtilService) {
        super('takenMijn', zoekenService, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = 'TAAK';
        zoekParameters.alleenMijnTaken = true;
    }
}
