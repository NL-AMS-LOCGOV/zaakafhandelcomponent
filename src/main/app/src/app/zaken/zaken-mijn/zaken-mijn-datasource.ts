/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {GebruikersvoorkeurenService} from '../../gebruikersvoorkeuren/gebruikersvoorkeuren.service';
import {Werklijst} from '../../gebruikersvoorkeuren/model/werklijst';
import {MatDialog} from '@angular/material/dialog';

/**
 * Datasource voor de mijn zaken. Via deze class wordt de data voor de tabel opgehaald
 */
export class ZakenMijnDatasource extends ZoekenTableDataSource<ZaakZoekObject> {

    constructor(zoekenService: ZoekenService,
                gebruikersvoorkeurenService: GebruikersvoorkeurenService,
                dialog: MatDialog,
                utilService: UtilService) {
        super(Werklijst.MIJN_ZAKEN, zoekenService, gebruikersvoorkeurenService, dialog, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = 'ZAAK';
        zoekParameters.alleenMijnZaken = true;
    }
}
