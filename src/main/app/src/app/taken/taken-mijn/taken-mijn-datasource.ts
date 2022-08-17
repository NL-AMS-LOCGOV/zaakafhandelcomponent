/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {Werklijst} from '../../gebruikersvoorkeuren/model/werklijst';
import {ZoekenDataSource} from '../../shared/dynamic-table/datasource/zoeken-data-source';

export class TakenMijnDatasource extends ZoekenDataSource<TaakZoekObject> {

    constructor(zoekenService: ZoekenService,
                utilService: UtilService) {
        super(Werklijst.MIJN_TAKEN, zoekenService, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = 'TAAK';
        zoekParameters.alleenMijnTaken = true;
    }
}
