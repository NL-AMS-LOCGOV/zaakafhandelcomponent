/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs';
import {ZaakOverzicht} from '../model/zaak-overzicht';
import {ZakenService} from '../zaken.service';
import {TableDataSource} from '../../shared/dynamic-table/datasource/table-data-source';
import {UtilService} from '../../core/service/util.service';

/**
 * Datasource voor de werkvoorraad zaken. Via deze class wordt de data voor de tabel opgehaald
 */
export class ZakenWerkvoorraadDatasource extends TableDataSource<ZaakOverzicht> {

    zoekParameters: {
        selectie: 'groep' | 'zaaktype',
        groep: string,
        zaaktype: string
    } = {
        selectie: 'groep',
        groep: null,
        zaaktype: null
    };

    selecties: string[] = ['groep', 'zaaktype'];

    constructor(private zakenService: ZakenService, private utilService: UtilService) {
        super();
    }

    load(): void {
        this.utilService.setLoading(true);
        this.zakenService.getWerkvoorraadZaken(this.getTableRequest())
            .pipe(
                catchError(() => of({data: [], totalItems: 0})),
                finalize(() => this.utilService.setLoading(false))
            ).subscribe(zaakResponse => {
                this.setData(zaakResponse);
            }
        );
    }

    zoekZaken(): void {
        this.removeFilter(this.zoekParameters.selectie === 'groep' ? 'zaaktype' : 'groep');
        this.setFilter(this.zoekParameters.selectie, this.zoekParameters[this.zoekParameters.selectie]);
    }

}

