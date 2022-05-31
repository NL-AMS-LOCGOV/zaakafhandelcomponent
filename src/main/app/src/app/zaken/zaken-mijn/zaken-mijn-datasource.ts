/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {TableDataSource} from '../../shared/dynamic-table/datasource/table-data-source';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs';
import {ZaakOverzicht} from '../model/zaak-overzicht';

export class ZakenMijnDatasource extends TableDataSource<ZaakOverzicht> {

    zoekParameters: { zaaktype: string } = {zaaktype: ''};

    constructor(private zakenService: ZakenService, private utilService: UtilService) {
        super();
    }

    load() {
        this.utilService.setLoading(true);

        this.zakenService.listZakenMijn(this.getTableRequest())
            .pipe(
                catchError(() => of({data: [], totalItems: 0})),
                finalize(() => this.utilService.setLoading(false)))
            .subscribe(zaakResponse => {
                this.setData(zaakResponse);
            });
    }

    searchCases() {
        this.filter('zaaktype', this.zoekParameters.zaaktype);
    }
}
