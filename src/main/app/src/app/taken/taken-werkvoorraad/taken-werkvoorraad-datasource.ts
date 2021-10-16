/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {catchError, finalize} from 'rxjs/operators';
import {of} from 'rxjs';
import {TableDataSource} from '../../shared/dynamic-table/datasource/table-data-source';
import {UtilService} from '../../core/service/util.service';
import {TakenService} from '../taken.service';
import {Taak} from '../model/taak';

/**
 * Data source for the Zaken view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class TakenWerkvoorraadDatasource extends TableDataSource<Taak> {

    constructor(private takenService: TakenService, private utilService: UtilService) {
        super();
    }

    load(): void {
        this.utilService.setLoading(true);
        this.takenService.getWerkvoorraadTaken(this.getTableRequest())
            .pipe(
                catchError(() => of({data: [], totalItems: 0})),
                finalize(() => this.utilService.setLoading(false))
            ).subscribe(taakResponse => {
                this.setData(taakResponse);
            }
        );
    }
}

