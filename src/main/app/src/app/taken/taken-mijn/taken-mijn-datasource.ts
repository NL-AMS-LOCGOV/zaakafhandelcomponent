/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {TaakZoekObject} from '../../zoeken/model/taken/taak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {GebruikersvoorkeurenService} from '../../gebruikersvoorkeuren/gebruikersvoorkeuren.service';
import {MatDialog} from '@angular/material/dialog';
import {Werklijst} from '../../gebruikersvoorkeuren/model/werklijst';

export class TakenMijnDatasource extends ZoekenTableDataSource<TaakZoekObject> {

    constructor(zoekenService: ZoekenService,
                gebruikersvoorkeurenService: GebruikersvoorkeurenService,
                dialog: MatDialog,
                utilService: UtilService) {
        super(Werklijst.MIJN_TAKEN, zoekenService, gebruikersvoorkeurenService, dialog, utilService);
    }

    protected initZoekparameters(zoekParameters: ZoekParameters) {
        zoekParameters.type = 'TAAK';
        zoekParameters.alleenMijnTaken = true;
    }
}
