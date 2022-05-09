/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';

export class WerklijstData {

    searchParameters: {
        selectie: 'groep' | 'zaaktype',
        groep: string,
        zaaktype: string
    };

    filters: {};

    columns: string;

    sorting: {
        column: string,
        direction: SortDirection
    };

    paginator: {
        page: number,
        pageSize: number
    };

}
