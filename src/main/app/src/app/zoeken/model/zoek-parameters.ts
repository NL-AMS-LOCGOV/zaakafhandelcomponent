/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {DatumVeld} from './datum-veld';
import {ZoekVeld} from './zoek-veld';
import {FilterVeld} from './filter-veld';
import {SorteerVeld} from './sorteer-veld';
import {ZoekObjectType} from './zoek-object-type';
import {DatumRange} from './datum-range';
import {FilterParameters} from './filter-parameters';

export class ZoekParameters {
    type: ZoekObjectType;
    alleenMijnZaken: boolean = false;
    alleenOpenstaandeZaken: boolean = false;
    alleenAfgeslotenZaken: boolean = false;
    alleenMijnTaken: boolean = false;
    zoeken: Partial<Record<ZoekVeld, string>> = {};
    filters: Partial<Record<FilterVeld, FilterParameters>> = {};
    datums: Partial<Record<DatumVeld, DatumRange>> = {};
    sorteerVeld: SorteerVeld;
    sorteerRichting: 'desc' | 'asc' | '';
    rows: number = 25;
    page: number = 0;
}
