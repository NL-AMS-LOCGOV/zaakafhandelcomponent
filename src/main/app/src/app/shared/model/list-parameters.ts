/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {SortDirection} from '@angular/material/sort';

export class ListParameters {
    sort: string;
    order: SortDirection;
    page: number;
    maxResults: number;

    constructor() {
        this.page = 0;
        this.maxResults = 25;
    }
}
