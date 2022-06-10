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
import {SessionStorageUtil} from '../../shared/storage/session-storage.util';

import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';

/**
 * Datasource voor de mijn zaken. Via deze class wordt de data voor de tabel opgehaald
 */
export class ZakenMijnDatasource extends ZoekenTableDataSource<ZaakZoekObject> {

    zoekParameters: ZoekParameters = SessionStorageUtil.getItem('zakenMijnZoekparameters', new ZoekParameters());

    constructor(private zoekenService: ZoekenService, private utilService: UtilService) {
        super();
    }

    getZoekParameters(): ZoekParameters {
        this.zoekParameters.alleenMijnZaken = true;
        this.zoekParameters.page = this.paginator.pageIndex;
        this.zoekParameters.rows = this.paginator.pageSize;
        this.zoekParameters.sorteerRichting = this.sort.direction;
        this.zoekParameters.sorteerVeld = this.sort.active;
        SessionStorageUtil.setItem('zakenMijnZoekparameters', this.zoekParameters);
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
        this.sort.active = this.zoekParameters.sorteerVeld;
        this.sort.direction = this.zoekParameters.sorteerRichting;
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = this.zoekParameters.rows;
        this.load();
    }

    initColumns(defaultColumns: Map<string, ColumnPickerValue>): void {
        this._initColumns('zakenMijnColumns', defaultColumns);
    }
}
