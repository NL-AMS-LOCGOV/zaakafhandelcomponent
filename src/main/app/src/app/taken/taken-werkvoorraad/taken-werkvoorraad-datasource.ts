/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';
import {Werklijst} from '../../gebruikersvoorkeuren/model/werklijst';

/**
 * Datasource voor de werkvoorraad taken. Via deze class wordt de data voor de tabel opgehaald
 */
export class TakenWerkvoorraadDatasource extends ZoekenTableDataSource<TaakZoekObject> {

    constructor(zoekenService: ZoekenService,
                utilService: UtilService) {
        super(Werklijst.WERKVOORRAAD_TAKEN, zoekenService, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = 'TAAK';
    }
}
