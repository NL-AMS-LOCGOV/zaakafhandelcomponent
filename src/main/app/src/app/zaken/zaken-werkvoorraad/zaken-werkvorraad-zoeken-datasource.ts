/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {finalize} from 'rxjs/operators';
import {UtilService} from '../../core/service/util.service';
import {ZoekenTableDataSource} from '../../shared/dynamic-table/datasource/zoeken-table-data-source';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {ZoekParameters} from '../../zoeken/model/zoek-parameters';
import {ZoekResultaat} from '../../zoeken/model/zoek-resultaat';

/**
 * Datasource voor de werkvoorraad zaken. Via deze class wordt de data voor de tabel opgehaald
 */
export class ZakenWerkvoorraadZoekenDatasource extends ZoekenTableDataSource<ZaakZoekObject> {

    constructor(private zoekenService: ZoekenService, private utilService: UtilService) {
        super();
    }

    zoekParameters: ZoekParameters = new ZoekParameters();

    getZoekParameters(): ZoekParameters {
        this.zoekParameters.filterQueries['zaak_afgehandeld'] = 'false';
        this.zoekParameters.start = this.paginator.pageIndex * this.paginator.pageSize;
        this.zoekParameters.rows = this.paginator.pageSize;
        return this.zoekParameters;
    }

    load(): void {
        this.utilService.setLoading(true);
        this.zoekenService.list(this.getZoekParameters())
            .pipe(
                finalize(() => this.utilService.setLoading(false))
            ).subscribe(zaakResponse => {
                this.setData(zaakResponse as ZoekResultaat<ZaakZoekObject>);
            }
        );
    }

    reset() {
        this.zoekParameters = new ZoekParameters();
        this.load();
    }
}

