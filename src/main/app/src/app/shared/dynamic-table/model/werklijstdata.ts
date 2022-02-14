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
    } = {
        selectie: 'groep',
        groep: null,
        zaaktype: null
    };

    filters: {};

    columns: string;

    sorting: {
        column: string,
        direction: SortDirection
    } = {
        column: '',
        direction: ''
    };

    paginator: {
        page: number,
        pageSize: number
    } = {
        page: 0,
        pageSize: 25
    };

}
